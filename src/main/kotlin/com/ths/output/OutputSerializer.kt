package com.ths.output

import com.fasterxml.jackson.databind.ObjectMapper
import com.ths.service.BranchAndNoOfChanges
import com.ths.service.ChangedPath
import java.io.File
import org.apache.log4j.Logger

private val log = Logger.getLogger(OutputSerializer::class.java)

object OutputSerializer {
    fun convertToOutputDtos(changes: Map<ChangedPath, Collection<BranchAndNoOfChanges>>): OutputDto {
        return OutputDto(changes.entries.map {
            PathOutputDto(it.key, it.value.map {
                BranchOutputDto(it.branchUrl, it.author, it.noOfChanges)
            }.toList().sortedBy { it.noOfChanges }.reversed())
        }.toList().sortedBy { it.path })
    }

}

fun OutputDto.writeToFile(outfile: File) {
    log.info("Writing output to ${outfile.absolutePath}")
    ObjectMapper().writeValue(outfile, this)
}
