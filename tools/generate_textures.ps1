# Generates the 16x16 Minecraft item textures for both apples.
Add-Type -AssemblyName System.Drawing

$outDir = Join-Path $PSScriptRoot '..\src\main\resources\assets\super_apples\textures\item'

$mask = @(
    '................',
    '.......SS.......',
    '......SLLS......',
    '.....LLLL.......',
    '....#######.....',
    '...#########....',
    '..###########...',
    '..###########...',
    '.#############..',
    '.#############..',
    '.#############..',
    '.#############..',
    '..###########...',
    '...#########....',
    '....#######.....',
    '................'
)

function Get-Clamped([double]$value) {
    if ($value -lt 0.0) { return 0.0 }
    if ($value -gt 1.0) { return 1.0 }
    return $value
}

function New-AppTexture {
    param(
        [string]$Name,
        [string]$OutlineHex,
        [string]$LightHex,
        [string]$DarkHex,
        [int]$FacetMode
    )

    $bitmap = New-Object System.Drawing.Bitmap 16,16
    $outline = [System.Drawing.ColorTranslator]::FromHtml($OutlineHex)
    $light = [System.Drawing.ColorTranslator]::FromHtml($LightHex)
    $dark = [System.Drawing.ColorTranslator]::FromHtml($DarkHex)
    $stemDark = [System.Drawing.Color]::FromArgb(255, 64, 40, 22)
    $stem = [System.Drawing.Color]::FromArgb(255, 112, 74, 40)
    $leafDark = [System.Drawing.Color]::FromArgb(255, 32, 96, 56)
    $leaf = [System.Drawing.Color]::FromArgb(255, 82, 168, 94)

    for ($y = 0; $y -lt 16; $y++) {
        $row = $mask[$y]
        for ($x = 0; $x -lt 16; $x++) {
            $ch = $row[$x]
            if ($ch -eq '.') { continue }

            if ($ch -eq 'S') {
                $color = if ($y -eq 1) { $stemDark } else { $stem }
            } elseif ($ch -eq 'L') {
                $color = if (($x + $y) % 3 -eq 0) { $leafDark } else { $leaf }
            } else {
                $isOutline = $false
                foreach ($neighbor in @(@(1,0), @(-1,0), @(0,1), @(0,-1))) {
                    $nx = $x + $neighbor[0]
                    $ny = $y + $neighbor[1]
                    if ($nx -lt 0 -or $nx -gt 15 -or $ny -lt 0 -or $ny -gt 15) {
                        $isOutline = $true
                        break
                    }
                    if ($mask[$ny][$nx] -eq '.') {
                        $isOutline = $true
                        break
                    }
                }

                if ($isOutline) {
                    $color = $outline
                } else {
                    $t = Get-Clamped (($y - 4) / 10.0)
                    $u = [Math]::Abs($x - 7.5) / 7.5
                    $k = Get-Clamped (0.45 * $t + 0.35 * $u)
                    $r = [int]($light.R + ($dark.R - $light.R) * $k)
                    $g = [int]($light.G + ($dark.G - $light.G) * $k)
                    $b = [int]($light.B + ($dark.B - $light.B) * $k)
                    $color = [System.Drawing.Color]::FromArgb(255, $r, $g, $b)

                    if ($x -le 4 -and $y -le 6) {
                        $color = [System.Drawing.Color]::FromArgb(
                            255,
                            [Math]::Min(255, $color.R + 28),
                            [Math]::Min(255, $color.G + 28),
                            [Math]::Min(255, $color.B + 28))
                    }

                    if ($FacetMode -eq 1) {
                        if (($x + $y) % 5 -eq 0 -or ($x - $y) % 5 -eq 0) {
                            $color = [System.Drawing.Color]::FromArgb(
                                255,
                                [Math]::Max(0, $color.R - 16),
                                [Math]::Max(0, $color.G - 12),
                                [Math]::Max(0, $color.B - 10))
                        }
                    } elseif ($FacetMode -eq 2) {
                        $hash = ($x * 7 + $y * 13) % 23
                        if ($hash -eq 0 -or $hash -eq 11) {
                            $color = [System.Drawing.Color]::FromArgb(255, 186, 150, 96)
                        } elseif (($x + $y) % 6 -eq 0) {
                            $color = [System.Drawing.Color]::FromArgb(
                                255,
                                [Math]::Max(0, $color.R - 14),
                                [Math]::Max(0, $color.G - 14),
                                [Math]::Max(0, $color.B - 14))
                        }
                    }
                }
            }

            $bitmap.SetPixel($x, $y, $color)
        }
    }

    $path = Join-Path $outDir "$Name.png"
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bitmap.Dispose()
    Write-Output "Generated $path"
}

New-AppTexture -Name 'diamond_apple' -OutlineHex '#0B2C54' -LightHex '#BEEBFF' -DarkHex '#1E5CA8' -FacetMode 1
New-AppTexture -Name 'netherite_apple' -OutlineHex '#0B0B0B' -LightHex '#A8A8A8' -DarkHex '#2B2B2B' -FacetMode 2
