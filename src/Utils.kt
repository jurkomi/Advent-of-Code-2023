import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Multiply all values in an [Iterable] of [Int]s.
 */
fun Iterable<Int>.multiply(): Int {
    var multiplication = 1
    for (element in this) {
        multiplication *= element
    }
    return multiplication
}

/**
 * Converts all the digits in the text to [Long] value. Returns 0 in case no digits are present.
 */
fun String.getAllDigitsAsLong(): Long =
    filter { it.isDigit() }.let { if (it.isEmpty()) 0 else it.toLong() }
