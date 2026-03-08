$PluginName = "WorldToolsPlugin"
$PaperApi = "c:/Users/g/Desktop/mc-server/libraries/io/papermc/paper/paper-api/1.21.11-R0.1-SNAPSHOT/paper-api-1.21.11-R0.1-SNAPSHOT.jar"

$Classpath = @(
    $PaperApi
    "c:/Users/g/Desktop/mc-server/libraries/com/google/code/gson/gson/2.13.2/gson-2.13.2.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/kyori/adventure-api/4.25.0/adventure-api-4.25.0.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/kyori/adventure-key/4.25.0/adventure-key-4.25.0.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-plain/4.25.0/adventure-text-serializer-plain-4.25.0.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-legacy/4.25.0/adventure-text-serializer-legacy-4.25.0.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/md-5/bungeecord-chat/1.21-R0.2-deprecated+build.21/bungeecord-chat-1.21-R0.2-deprecated+build.21.jar"
    "c:/Users/g/Desktop/mc-server/libraries/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar"
) -join ";"

$SrcDir = "c:/Users/g/Desktop/mc-server/plugins-source/WorldToolsPlugin/src"
$BuildDir = "c:/Users/g/Desktop/mc-server/plugins-source/WorldToolsPlugin/target"
$OutputDir = "c:/Users/g/Desktop/mc-server/plugins"

# Tools
$Javac = "C:\Program Files\Java\jdk-21.0.10\bin\javac.exe"
$Jar = "C:\Program Files\Java\jdk-21.0.10\bin\jar.exe"

# Clean
Write-Host "Cleaning..."
if (Test-Path $BuildDir) { Remove-Item -Recurse -Force $BuildDir }
New-Item -ItemType Directory -Path "$BuildDir/classes" | Out-Null

# Find sources
Write-Host "Finding Java files..."
$Sources = Get-ChildItem -Path "$SrcDir/main/java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
if ($Sources -eq $null -or $Sources.Count -eq 0) {
    Write-Host "No Java files found"
    exit 0
}
$Sources | Out-File -FilePath "$BuildDir/sources.txt" -Encoding ascii

# Compile
Write-Host "Compiling..."
$javacArgs = @("-d", "$BuildDir/classes", "-cp", $Classpath, "-Xlint:deprecation", "@$BuildDir/sources.txt")
& $Javac $javacArgs
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed!"
    exit 1
}

# Copy resources
if (Test-Path "$SrcDir/main/resources") {
    Write-Host "Copying resources..."
    Copy-Item -Path "$SrcDir/main/resources/*" -Destination "$BuildDir/classes" -Recurse -Force
}

# Create JAR
Write-Host "Creating JAR..."
$jarFile = "$BuildDir/$PluginName.jar"
if (Test-Path $jarFile) { Remove-Item $jarFile }

Push-Location "$BuildDir/classes"
& $Jar cvf $jarFile .
Pop-Location

# Copy to plugins
Write-Host "Copying to plugins folder..."
Copy-Item -Path "$BuildDir/$PluginName.jar" -Destination "$OutputDir/" -Force

Write-Host "Done! Plugin copied to: $OutputDir/$PluginName.jar"
