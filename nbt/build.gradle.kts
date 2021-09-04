plugins {
  id("adventure.common-conventions")
}

dependencies {
  api("net.kyori:examination-api:1.3.0")
  api("net.kyori:examination-string:1.3.0")
  compileOnlyApi("org.jetbrains:annotations:21.0.1")
}

applyJarMetadata("net.kyori.adventure.nbt")
