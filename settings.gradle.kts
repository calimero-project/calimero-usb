rootProject.name = "calimero-usb"
include("lib")

fun safeIncludeBuild(dir: String) {
	if (file(dir).exists()) includeBuild(dir)
}

safeIncludeBuild("../calimero-core")
