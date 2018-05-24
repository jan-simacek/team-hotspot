package com.ths.model

enum class ChangeType(val symbol: Char) {
    ADD('A'),
    MODIFY('M'),
    BRANCH('B'),
    COPY('C'),
    DELETE('D'),
    REPLACE('R'),
    OTHER('O');

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