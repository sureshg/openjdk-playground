@import dev.suresh.jte.Config
@import dev.suresh.jte.KtConfig
@import java.time.LocalDateTime

@param Config config
@param KtConfig ktConfig

App Version  = ${ktConfig.getAppVersion()}
${config.language()} = ${config.version()}
${ktConfig.getLanguage()} = ${ktConfig.getVersion()}
Rendered By Java Jte on ${LocalDateTime.now().toString()}