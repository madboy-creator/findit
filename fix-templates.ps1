# Fix Thymeleaf fragment syntax in all HTML files
$files = Get-ChildItem -Path "src\main\resources\templates" -Recurse -Filter "*.html"

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $updated = $content -replace 'th:replace="([^"]+)"', 'th:replace="~{$1}"'
    $updated = $updated -replace 'th:insert="([^"]+)"', 'th:insert="~{$1}"'
    Set-Content $file.FullName $updated -NoNewline
    Write-Host "Updated: $($file.Name)"
}

Write-Host "Done! All templates updated."