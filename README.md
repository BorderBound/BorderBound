<div align='center'>
	<h2>BorderBound - Puzzle Game</h2>
    <img src="https://raw.githubusercontent.com/BorderBound/BorderBound/master/assets/ic_launcher.svg" width="150" />
</div>
<div align='center'>
    <p>
        <!-- <a href='https://apt.izzysoft.de/fdroid/index/apk/app.mlauncher'><img src='https://codeworkscreativehub.github.io/mLauncher/IzzyOnDroid.png' width="150" alt="IzzyOnDroid"></a> -->
        <a href='http://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/BorderBound/BorderBound'><img src='https://codeworkscreativehub.github.io/mLauncher/obtanium.png' width="150" alt="Obtanium"></a>
    	<!-- <a href='https://play.google.com/store/apps/details?id=app.mlauncher'><img src='https://codeworkscreativehub.github.io/mLauncher/google_play.png' width="150" alt="Obtanium"></a>	     -->
    	<!-- <a href='https://f-droid.org/packages/app.mlauncher'><img src='https://codeworkscreativehub.github.io/mLauncher/fdroid.png' width="150" alt="fDroid"></a> -->
    </p>
    <div align='center'>
        <p>
            <img src='https://img.shields.io/badge/Android-SDK_36-BD93F9?style=flat-square&logo=android&logoColor=white' alt="SDK-36">
            <a href='https://github.com/BorderBound/BorderBound/blob/main/LICENSE'><img src='https://img.shields.io/github/license/BorderBound/BorderBound?color=BD93F9&style=flat-square' alt="LICENSE"></a>
            <br>
            <!-- <a href='https://github.com/CodeWorksCreativeHub/mLauncher/releases/latest'><img src='https://img.shields.io/github/downloads/CodeWorksCreativeHub/mLauncher/total?color=50FA7B&style=flat-square&label=Overall&logo=github' alt="releases"></a> -->
            <!-- <a href='https://github.com/CodeWorksCreativeHub/mLauncher/releases/latest'><img src="https://img.shields.io/github/downloads/CodeWorksCreativeHub/mLauncher/latest/total?color=50FA7B&style=flat-square&label=Latest&logo=github" alt="GitHub Downloads (all assets, latest release)"></a> -->
			<!-- <br> -->
			<!-- <a href='https://play.google.com/store/apps/details?id=app.mlauncher'><img src='https://img.shields.io/endpoint?url=https%3A%2F%2Fplay.cuzi.workers.dev%2Fplay%3Fi%3Dapp.mlauncher%26gl%3DUK%26hl%3Den%26l%3D%24name%26m%3D%24totalinstalls&color=50FA7B&style=flat-square&label=Google%20Play&logo=googleplay' alt='Google Play'></a> -->
            <!-- <br> -->
	    	<!-- <a href='https://apt.izzysoft.de/fdroid/index/apk/app.mlauncher'><img alt="IzzyOnDroid (including pre-releases)" src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/app.mlauncher&color=FFB86C&style=flat-square&label=IzzyOnDroid"></a> -->
            <a href='https://github.com/BorderBound/BorderBound/releases/latest'><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/BorderBound/BorderBound?color=FFB86C&style=flat-square&label=Github"></a>
	    	<!-- <br> -->
			<!-- <a href='https://play.google.com/store/apps/details?id=app.mlauncher'><img src='https://img.shields.io/endpoint?url=https%3A%2F%2Fplay.cuzi.workers.dev%2Fplay%3Fi%3Dapp.mlauncher%26gl%3DUK%26hl%3Den%26l%3D%24name%26m%3D%24version&color=FFB86C&style=flat-square&label=Google%20Play' alt='Google Play'></a> -->
            <!-- <a href='https://gitlab.com/fdroid/fdroiddata/-/blob/master/metadata/app.mlauncher.yml'><img alt="F-Droid (including pre-releases)" src="https://img.shields.io/f-droid/v/app.mlauncher?color=FFB86C&style=flat-square&label=F-Droid"></a> -->
            <br>
            <img src='https://img.shields.io/badge/Maintained-yes-FF5555?style=flat-square' alt="Maintained">
        </p>
    </div>
</div>

Easy to learn, hard to master. Play the addicting puzzle game BorderBound now and to think outside the box.
There are already over 100 levels available and even more to come. The game features a clean and elegant design - you will be amazed by the simplicity and complexity at the same time. 
Fill all boxes with the color of their border by using the special boxes. Those, for example, fill surrounding boxes in only one direction with their color. Discover various types of special boxes in the provided level packs.

## Contributing levels

### Level editor

Levels can easily be created on [Level-Editor](https://borderbound.github.io/Level-Editor/) using a visual editor.

### Level definitions

Alternatively, levels can be designed in xml.

```
<level number="0"
    color="b0ooo
           b0000
           b0r0g
           b0r0g
           b0r0g
           00000"
    modifier="
           0X00L
           0XXXX
           0XDX0
           0X0X0
           UX0XU
           XXXXX" />
```

| Modifier  | Action |
|--------------|--------|
| 0 | No modifier |
| X | Field disabled |
| F | Flood |
| U | Up |
| R | Right |
| L | Left |
| D | Down |
| B | Bomb |
| w | Up |
| x | Right |
| a | Left |
| s | Down |

### Level packs

- Easy: No need to take away blocks with a modifier that did not create the block
- Hard: I found it hard to solve (even though I designed the level)
- Medium: Everything else
- Community: Contributed levels

## License

This app is licensed under the GPL v3.
