package com.ths.model

enum class ChangeType {
    ADD, MODIFY, BRANCH, COPY, DELETE;

    companion object {
        fun findByChar(type: Char): ChangeType = when(type){
            'A' -> ADD
            'C' -> COPY
            'M' -> MODIFY
            'D' -> DELETE
            else -> throw IllegalArgumentException("Unknown change type $type")
        }
    }
}

data class Change(val path: String, val type: ChangeType, val copyPath: String?, val copyRev: Long?)