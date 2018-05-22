package com.ths.model

enum class ChangeType {
    ADD, MODIFY, BRANCH, COPY, DELETE, REPLACE, OTHER;

    companion object {
        fun findByChar(type: Char): ChangeType = when(type){
            'A' -> ADD
            'C' -> COPY
            'M' -> MODIFY
            'D' -> DELETE
            'R' -> REPLACE
            else -> OTHER
        }
    }
}

data class Change(val path: String, val type: ChangeType, val copyPath: String?, val copyRev: Long?)