package com.gyvacha.androidssh.utils

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.RSAKeyParameters
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil
import org.bouncycastle.crypto.util.PrivateKeyFactory
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder
import java.io.StringWriter
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.util.Base64

class SshKeyGenerator {
    companion object {
        enum class Algorithm(val title: String) {
            ALGORITHM_RSA("RSA"),
            ALGORITHM_ED25519("Ed25519")
        }
        private const val CHUNKED_SIZE = 70
    }

    fun generateRsaKeyPair(keySize: Int = 4096): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA", "BC")
        keyGen.initialize(keySize, SecureRandom.getInstanceStrong())
        return keyGen.generateKeyPair()
    }

    fun generateEd25519KeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("Ed25519", "BC")
        return keyGen.generateKeyPair()
    }

    fun convertToOpenSshPublicKey(keyPair: KeyPair): String {
        return when (keyPair.public.algorithm) {
            Algorithm.ALGORITHM_RSA.title -> {
                val privateParam = PrivateKeyFactory.createKey(keyPair.private.encoded) as RSAPrivateCrtKeyParameters
                val pubParam = RSAKeyParameters(false, privateParam.modulus, privateParam.publicExponent)
                val pubBytes = OpenSSHPublicKeyUtil.encodePublicKey(pubParam)
                "ssh-rsa " + Base64.getEncoder().encodeToString(pubBytes)
            }

            Algorithm.ALGORITHM_ED25519.title -> {
                val privateParam = PrivateKeyFactory.createKey(keyPair.private.encoded) as Ed25519PrivateKeyParameters
                val pubParam = privateParam.generatePublicKey()
                val pubBytes = OpenSSHPublicKeyUtil.encodePublicKey(pubParam)
                "ssh-ed25519 " + Base64.getEncoder().encodeToString(pubBytes)
            }

            else -> error("Unsupported key algorithm: ${keyPair.private.algorithm}")
        }
    }

    fun privateKeyPem(keyPair: KeyPair, passphrase: String? = null): String {
        return when (keyPair.private.algorithm) {
            Algorithm.ALGORITHM_RSA.title -> {
                val sw = StringWriter()
                val pemWriter = JcaPEMWriter(sw)
                if (passphrase != null) {
                    pemWriter.writeObject(
                        keyPair.private,
                        JcePEMEncryptorBuilder("AES-256-CFB")
                            .setProvider("BC")
                            .build(passphrase.toCharArray())
                    )
                } else {
                    pemWriter.writeObject(keyPair.private)
                }
                pemWriter.close()
                sw.toString()
            }

            Algorithm.ALGORITHM_ED25519.title -> {
                if (passphrase == null) {
                    val privParam = PrivateKeyFactory.createKey(keyPair.private.encoded)
                    val encoded = OpenSSHPrivateKeyUtil.encodePrivateKey(privParam)
                    val base64 = Base64.getEncoder().encodeToString(encoded)
                    "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                        base64.chunked(CHUNKED_SIZE).joinToString("\n") +
                        "\n-----END OPENSSH PRIVATE KEY-----\n"
                } else {
                    val sw = StringWriter()
                    val pemWriter = JcaPEMWriter(sw)
                    pemWriter.writeObject(
                        keyPair.private,
                        JcePEMEncryptorBuilder("AES-256-CFB")
                            .setProvider("BC")
                            .build(passphrase.toCharArray())
                    )
                    pemWriter.close()
                    sw.toString()
                }
            }

            else -> error("Unsupported key algorithm: ${keyPair.private.algorithm}")
        }
    }
}
