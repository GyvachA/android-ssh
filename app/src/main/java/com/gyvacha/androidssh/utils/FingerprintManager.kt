package com.gyvacha.androidssh.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class FingerprintManager(
    context: Context
) {

    private val knownHostsFile = File(context.filesDir, "known_hosts").apply {
        if (!exists()) createNewFile()
    }
    private val _fingerprints = MutableStateFlow<Map<String, String>>(emptyMap())
    val fingerprints: StateFlow<Map<String, String>> = _fingerprints.asStateFlow()

    init {
        loadKnownHosts()
    }

    private fun loadKnownHosts() {
        val map = mutableMapOf<String, String>()
        knownHostsFile.forEachLine { line ->
            val parts = line.split(" ")
            if (parts.size >= 2) map[parts[0]] = parts[1]
        }
        _fingerprints.value = map
    }

    fun isKnown(host: String, fingerprint: String) = _fingerprints.value[host] == fingerprint

    fun add(host: String, fingerprint: String) {
        val map = _fingerprints.value.toMutableMap()
        map[host] = fingerprint
        _fingerprints.value = map
        knownHostsFile.writeText(map.entries.joinToString("\n") { "${it.key} ${it.value}" } + "\n")
    }

    fun remove(host: String) {
        val map = _fingerprints.value.toMutableMap()
        map.remove(host)
        _fingerprints.value = map
        knownHostsFile.writeText(map.entries.joinToString("\n") { "${it.key} ${it.value}" } + "\n")
    }
}
