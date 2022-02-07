[![](https://www.jitpack.io/v/wuhongsheng/HitAnimation.svg)](https://www.jitpack.io/#wuhongsheng/HitAnimation)
# HitAnimation
命中动画（帧动画）组件，常用于车机语音交互中

## 导入方式
### 将JitPack存储库添加到您的构建文件中(项目根目录下build.gradle文件)
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 添加依赖项

```
dependencies {
    implementation 'com.github.wuhongsheng:HitAnimation:-SNAPSHOT'
}
```

## 示例代码

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var tv:TextView = findViewById(R.id.tv)
        var tvPlay:TextView  = findViewById(R.id.tv_playAnimation)
        tvPlay.setOnClickListener(View.OnClickListener {
            Log.i("MainActivity", "onCreate: ")
            HitAnimationHelper.getInstance(this).startAnimation(tv)
        })
    }
}
```

