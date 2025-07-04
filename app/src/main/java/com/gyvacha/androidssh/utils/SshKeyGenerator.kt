package com.gyvacha.androidssh.utils

import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.StringWriter
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64

object SshKeyGenerator {
    enum class Algorithm(val title: String) {
        ALGORITHM_RSA("RSA"),
        ALGORITHM_ED25519("Ed25519")
    }

    fun generateRsaKeyPair(keySize: Int = 2048): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA", "BC")
        keyGen.initialize(keySize)
        return keyGen.generateKeyPair()
    }

    fun generateEd25519KeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("Ed25519", "BC")
        return keyGen.generateKeyPair()
    }

    fun convertToOpenSshPublicKey(keyPair: KeyPair): String {
        return when (keyPair.public.algorithm) {
            Algorithm.ALGORITHM_ED25519.title -> {
                val publicEncoded = keyPair.public.encoded
                val base64 = Base64.getEncoder().encodeToString(publicEncoded)
                "ssh-ed25519 $base64"
            }
            Algorithm.ALGORITHM_RSA.title -> {
                val encoded = keyPair.public.encoded
                val subjectPublicKeyInfo = org.bouncycastle.asn1.x509.SubjectPublicKeyInfo.getInstance(encoded)
                val rsaPubKey = RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey())
                val byteStream = ByteArrayOutputStream()
                val dos = DataOutputStream(byteStream)
                val sshRsaString = "ssh-rsa".toByteArray()
                dos.writeInt(sshRsaString.size)
                dos.write(sshRsaString)
                val pubExp = rsaPubKey.publicExponent.toByteArray()
                val pubModulus = rsaPubKey.modulus.toByteArray()
                dos.writeInt(pubExp.size)
                dos.write(pubExp)
                dos.writeInt(pubModulus.size)
                dos.write(pubModulus)
                val base64 = Base64.getEncoder().encodeToString(byteStream.toByteArray())
                "ssh-rsa $base64"
            }
            else -> throw IllegalStateException("Unsupported key algorithm: ${keyPair.public.algorithm}")
        }
    }

    fun privateKeyPem(keyPair: KeyPair, passphrase: String? = null): String {
        val sw = StringWriter()
        val pemWriter = JcaPEMWriter(sw)
        if (passphrase != null) {
            val encryptor = JcePEMEncryptorBuilder("AES-256-CFB")
                .setProvider("BC")
                .build(passphrase.toCharArray())
            pemWriter.writeObject(keyPair.private, encryptor)
        } else {
            pemWriter.writeObject(keyPair.private)
        }
        pemWriter.close()
        return sw.toString()
    }
}