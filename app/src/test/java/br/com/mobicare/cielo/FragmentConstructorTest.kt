package br.com.mobicare.cielo

import org.junit.Assert
import org.junit.Test
import java.io.File


val overrideStringPatterns = arrayListOf("Fragment(v", "Fragment(p")
const val MAIN_DIRECTORY = "./src/main/java"

class FragmentConstructorTest {

    @Test
    fun `Assert that all Fragments have empty constructors`() {
        val files = arrayListOf<File?>()

        getFragmentFiles(MAIN_DIRECTORY, files)

        val lines = extractClassDefinitionLines(files)

        val failedFragments = filterInvalidFragments(lines)

        failedFragments.forEach {
            println("$it possui construtor sobrescrito")
        }

        Assert.assertTrue(failedFragments.isEmpty())
    }

    private fun getFragmentFiles(directoryName: String?, files: ArrayList<File?>) {
        val directory = File(directoryName.toString())

        val fList: Array<File> = directory.listFiles() as Array<File>
        for (file in fList) {
            if (file.isFile) {
                if (file.isFragment()) {
                    files.add(file)
                }
            } else if (file.isDirectory) {
                getFragmentFiles(file.absolutePath, files)
            }
        }
    }

    private fun extractClassDefinitionLines(files: ArrayList<File?>): ArrayList<String> {
        val lines = arrayListOf<String>()
        files.forEach { file ->
            file?.bufferedReader().use { reader ->
                run {
                    val fileLines = reader?.readLines()
                    val classLine = fileLines?.firstOrNull { it.contains("class") }
                    classLine?.let { lines.add(it) }
                }
            }
        }
        return lines
    }

    private fun filterInvalidFragments(lines: ArrayList<String>): ArrayList<String> {
        val failedFragments = arrayListOf<String>()
        lines.forEach { line ->
            val className = line.split(" ")[1].removeSuffix("(")
            if (isConstructorOverriden(line)) {
                failedFragments.add(className)
            }
        }
        return ArrayList(failedFragments.distinct())
    }

    private fun File.isFragment(): Boolean {
        var isSubClassOfFragment = false
        var isValidFragment: Boolean
        this.bufferedReader().use { reader ->
            run {
                var fileLine = reader.readLines().joinToString("")
                fileLine = fileLine.filter { it.isWhitespace().not() }
                val parentHierarchy = arrayOf(
                    "):Fragment",
                    "):AppCompatFragment",
                    "):BaseFragment",
                    "):BottomSheetDialogFragment"
                )
                parentHierarchy.forEach { extendString ->
                    val isSubFragment = fileLine.contains(extendString)
                    isValidFragment = fileLine.contains(":FragmentManager").not()
                            && fileLine.contains(":FragmentPagerAdapter").not()
                            && fileLine.contains(":FragmentStateAdapter").not()
                    if (isSubFragment && isValidFragment) {
                        isSubClassOfFragment = true
                    }
                }
            }
        }
        return this.name.contains("Fragment") || isSubClassOfFragment
    }

    private fun isConstructorOverriden(classDefinitionLine: String): Boolean {
        val hasLineBreak = classDefinitionLine.last().toString() == "("
        if (hasLineBreak) {
            return true
        }
        overrideStringPatterns.forEach {
            val isCommentedClass = classDefinitionLine.contains("//")
            return isCommentedClass.not() && classDefinitionLine.contains(it)
        }
        return false
    }
}