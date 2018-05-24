package com.ths.model

import java.util.*

data class Revision(val number: Long, val author: String, val date: Date, val changes: List<Change>) {
    fun isTrunkBranch(): Boolean = changes.findBranch() != null
    fun isForUrl(branchUrl: String) = changes.all { it.path.startsWith("$branchUrl/") }
    fun hasDeleteOf(path: String): Boolean = changes.any { it.type == ChangeType.DELETE  && it.path == path}

    val trunkRevision: Long?
        get() = changes.findBranch()?.copyRev

    val branchUrl: String?
        get() = changes.findBranch()?.path

    private fun List<Change>.findBranch() = find { it.type == ChangeType.BRANCH }

    fun relativizePaths(branch: Branch): Revision {
        return Revision(number, author, date, changes.map {
            val relativizedPath = if(it.path.startsWith(branch.baseUrl))
                it.path.substring(branch.baseUrl.length)
            else
                it.path
            Change(relativizedPath, it.type, it.copyPath, it.copyRev)
        })
    }
}