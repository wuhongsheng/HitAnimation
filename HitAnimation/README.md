# HitAnimation
HitAnimation 是命中动画封装Moudle,支持自定义动画资源、坐标偏移、CallBackListener等设置
使用私有Maven依赖

## 导入方式

#### 本地local.properties配置nexus_password字段
```
nexus_password=###
```

#### gradle.properties 配置Maven
```
# maven config
SNAPSHOTS_REPOSITORY_URL=http://192.168.72.112:10890/nexus/content/repositories/android-snapshots/
RELEASE_REPOSITORY_URL=http://192.168.72.112:10890/nexus/content/repositories/android-release/
NEXUS_USERNAME=android
```

#### project gradle配置文件增加Maven配置
```
allprojects {
    //获取本地配置
    def localPropertiesFile = rootProject.file("local.properties")
    def localProperties = new Properties()
    localProperties.load(new FileInputStream(localPropertiesFile))
    def nexusPassword = localProperties['nexus_password']
    def gradlePropertiesFile = rootProject.file("gradle.properties")
    def gradleProperties = new Properties()
    gradleProperties.load(new FileInputStream(gradlePropertiesFile))
    def nexusUsername = gradleProperties['NEXUS_USERNAME']

    repositories {
        maven {
            url SNAPSHOTS_REPOSITORY_URL
            credentials {
                username nexusUsername
                password nexusPassword
            }
        }

        maven {
            url RELEASE_REPOSITORY_URL
            credentials {
                username nexusUsername
                password nexusPassword
            }
        }
    }
}
```
#### 添加依赖项

```
dependencies {
    implementation 'com.aichiptech.ui:hitanimation:-SNAPSHOT'
}
```


## 示例代码

```java
//构造方法
HitAnimationHelper hitAnimationHelper = new HitAnimationHelper.Builder(getActivity())
                 //设置锚点View(必填)
                 .setAnchorView(view)
                 //设置动画资源(可选。不设置使用默认资源)
                 .setAnimationId(R.drawable.hitanimation_default)
                 //设置坐标偏移量(可选)
                 setOffset(10,10)
                 //回调方法
                .setCallBackListener(new HitAnimationHelper.CallBackListener() {
                    @Override
                    public void onAnimationFinish(View mAnchor) {
                        if (mAnchor instanceof SwitchButton) {
                            SwitchButton view = (SwitchButton) mAnchor;
                            view.doSwitch();
                        } else {
                            mAnchor.performClick();
                        }
                    }
                })
                .build();
//两种调用方式
hitAnimationHelper.handleEvent();
hitAnimationHelper.handleEvent(view);
```