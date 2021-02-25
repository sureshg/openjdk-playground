@import dev.suresh.jte.Config
@import dev.suresh.jte.KtConfig
@import java.time.LocalDateTime

@param Config config
@param KtConfig ktConfig

Hello ${config.language()} - ${config.version()}
Hello ${ktConfig.getLanguage()} - ${ktConfig.getVersion()}
Rendered By Jte on ${LocalDateTime.now().toString()}
