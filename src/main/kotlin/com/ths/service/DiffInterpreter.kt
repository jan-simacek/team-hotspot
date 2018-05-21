package com.ths.service

object DiffInterpreter {
    /**
     * @return how many lines were changed in the input diff
     */
    fun retrieveNumLinesChanged(diff: String): Int =
        diff.lines().filter {
            it != "" && (it[0] == '+' || it[0] == '-')
        }.count()
}