package io.github.fvasco.pinpoi.importer

import android.util.Log
import io.github.fvasco.pinpoi.util.ZipGuardInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Import ZIP collection and KMZ file

 * @author Francesco Vasco
 */
class ZipImporter : AbstractImporter() {

    @Throws(IOException::class)
    override fun importImpl(inputStream: InputStream) {
        val zipInputStream = ZipInputStream(inputStream)
        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        while (zipEntry != null) {
            val entryName = zipEntry.name
            if (!zipEntry.isDirectory && !entryName.startsWith(".")) {
                val importer = ImporterFacade.createImporter(entryName, fileFormatFilter)
                if (importer != null) {
                    Log.d(ZipImporter::class.java.simpleName, "Import entry $entryName")
                    importer.configureFrom(this)
                    importer.importImpl(ZipGuardInputStream(zipInputStream))
                }
            }
            zipEntry = zipInputStream.nextEntry
        }
    }
}