package com.ths.model

class Branch(val baseUrl: String, val trunkRev: Long, val author: String, _revisions: List<Revision>) {
    val revisions = _revisions.map { it.relativizePaths(this) }

    override fun toString(): String {
        return "Branch(baseUrl='$baseUrl', trunkRev=$trunkRev, autor=$author, revisions=$revisions)"
    }

}