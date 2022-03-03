import GithubAction.MsgType.*
import java.nio.file.*
import java.nio.file.StandardOpenOption.*
import java.util.UUID


/**
 * Workflow commands for GitHub Actions.
 *
 * @see
 *   [WorkflowCommands](https://docs.github.com/en/actions/using-workflows/workflow-commands-for-github-actions)
 *   for more details.
 */
object GithubAction {

  /** Returns `true` if the running on Github action workflow. */
  val isEnabled = System.getenv("GITHUB_ACTIONS").toBoolean()

  /**
   * Prints a debug message to the log. You must create a secret named
   * **ACTIONS_STEP_DEBUG** with the value `true` to see the debug
   * messages set by this command in the log.
   */
  fun debug(message: String) = echo(message(message, DEBUG))

  /**
   * Creates a notice message and prints the message to the log. This
   * message will create an annotation, which can associate the message
   * with a particular file in your repository. Optionally, your message
   * can specify a position within the file.
   */
  fun notice(
    message: String,
    title: String = "",
    file: String = "",
    line: Int = 0,
    endLine: Int = 0,
    col: Int = 0,
    endColumn: Int = 0
  ) = echo(
    message(
      message = message,
      type = NOTICE,
      title = title,
      file = file,
      line = line,
      endLine = endLine,
      col = col,
      endColumn = endColumn
    )
  )

  /**
   * Creates a warning message and prints the message to the log. This
   * message will create an annotation, which can associate the message
   * with a particular file in your repository. Optionally, your message
   * can specify a position within the file.
   */
  fun warning(
    message: String,
    title: String = "",
    file: String = "",
    line: Int = 0,
    endLine: Int = 0,
    col: Int = 0,
    endColumn: Int = 0
  ) = echo(
    message(
      message = message,
      type = WARNING,
      title = title,
      file = file,
      line = line,
      endLine = endLine,
      col = col,
      endColumn = endColumn
    )
  )

  /**
   * Creates an error message and prints the message to the log. This
   * message will create an annotation, which can associate the message
   * with a particular file in your repository. Optionally, your message
   * can specify a position within the file.
   */
  fun error(
    message: String,
    title: String = "",
    file: String = "",
    line: Int = 0,
    endLine: Int = 0,
    col: Int = 0,
    endColumn: Int = 0
  ) = echo(
    message(
      message = message,
      type = ERROR,
      title = title,
      file = file,
      line = line,
      endLine = endLine,
      col = col,
      endColumn = endColumn
    )
  )

  /** Sets a Github Action's output parameter. */
  fun setOutput(name: String, value: Any) = echo("::set-output name=$name::$value")

  /** Creates an expandable group with a title in the log. */
  fun group(title: String, logs: List<String>) {
    if (isEnabled) {
      println("::group::$title")
      logs.forEach(::println)
      println("::endgroup::")
    }
  }

  /**
   * Masking a string. Masked word separated by whitespace is replaced
   * with the * character.
   */
  fun mask(message: String) = "::add-mask::$message"

  /** Prints message to the Github Action workflow log. */
  fun echo(message: String, mask: Boolean = false) {
    if (isEnabled) {
      // Replace line feed in multiline strings.
      val msg = when (mask) {
        true -> mask(message)
        else -> message
      }.replace("\\R", "%0A")
      println(msg)
    }
  }

  /**
   * Stops processing any workflow commands. This special command
   * allows you to log anything without accidentally running a workflow
   * command.
   */
  fun stopCommand(messages: List<String>) {
    if (isEnabled) {
      val token = UUID.randomUUID().toString()
      println(
        """
        |::stop-commands::$token
        |${messages.joinToString(System.lineSeparator())}
        |::$token::
        """.trimMargin()
      )
    }
  }

  /**
   * Setting an environment variable available to any subsequent steps
   * in a workflow.The step that creates or updates the environment
   * variable does not have access to the new value, but all subsequent
   * steps in a job will have access. The names of environment variables
   * are case-sensitive, and you can include punctuation.
   */
  fun setEnv(name: String, value: String) {
    if (isEnabled) {
      val ghActionEnv = System.getenv("GITHUB_ENV")
      debug("Writing to Github Action env file $ghActionEnv")

      val env = when {
        // Multiline string
        value.lines().size > 1 -> """
          |${name}<<EOF
          |$value
          |EOF
          """.trimMargin()
        else -> "$name=$value${System.lineSeparator()}"
      }
      Files.writeString(Path.of(ghActionEnv), env, Charsets.UTF_8, CREATE, APPEND)
    }
  }

  /**
   * Prepends a directory to the system `PATH` variable and
   * automatically makes it available to all subsequent actions in the
   * current job. The currently running action cannot access the updated
   * path variable. To see the currently defined paths for your job, you
   * can use **echo "$PATH"** in a step or an action.
   */
  fun addPath(path: String) {
    if (isEnabled && path.isNotBlank()) {
      val ghActionEnv = System.getenv("GITHUB_PATH")
      debug("Writing to Github Action path file $ghActionEnv")
      Files.writeString(Path.of(ghActionEnv), path, Charsets.UTF_8, CREATE, APPEND)
    }
  }

  /**
   * Creates a Github Action workflow message to log. The message
   * (except [DEBUG]) will create an annotation, which can associate the
   * message with a particular file in your repository. Optionally, your
   * message can specify a position within the file.
   *
   * @param message message string
   * @param type message type [MsgType]
   * @param title Custom title
   * @param file Filename
   * @param col Column number, starting at 1
   * @param endColumn End column number
   * @param line Line number, starting at 1
   * @param endLine End line number
   */
  private fun message(
    message: String,
    type: MsgType,
    title: String = "",
    file: String = "",
    line: Int = 0,
    endLine: Int = 0,
    col: Int = 0,
    endColumn: Int = 0,
  ) = buildString {
    append("::")
    append(type.name.toLowerCase())
    when {
      type != DEBUG -> {
        val params = mutableListOf<String>()
        if (title.isNotBlank())
          params.add("title=$title")
        if (file.isNotBlank()) {
          params.add("file=$file")
          if (line > 0) params.add("line=$line")
          if (endLine > 0) params.add("endLine=$endLine")
          if (col > 0) params.add("col=$col")
          if (endColumn > 0) params.add("endColumn=$endColumn")
        }
        append(" ")
        append(params.joinToString(","))
      }
    }
    append("::")
    append(message)
  }

  internal enum class MsgType {
    DEBUG, NOTICE, WARNING, ERROR
  }
}
