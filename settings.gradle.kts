rootProject.name = "calimero-usb"

fun safeIncludeBuild(dir: String) {
	if (file(dir).exists()) includeBuild(dir)
}

safeIncludeBuild("../calimero-core")
