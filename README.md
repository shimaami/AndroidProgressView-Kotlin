# AndroidProgressView-Kotlin
Progress view with arc, line and circle shapes and gradient effect

### Java version
Check [here](https://github.com/shimaami/AndroidProgressView)

# Attributes
| Name  | Description | Type | Default | Range |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| pvDirection  | Direction of the progress  | enum | fromLeft (clockwise) | fromLeft, fromRight |
| pvShape  | Shape of the progress view  | enum | arc | arc, circle, line |
| pvProgress  | Progress value  | float | 0 | 0 to 1 |
| pvBackgroundColor  | Progress background color  | color | Color.BLACK | - |
| pvProgressColor  | Progress color  | color | Color.RED | - |
| pvBackgroundWidth  | Progress background width  | dimension | 2dp | - |
| pvProgressWidth  | Progress width  | dimension | 10dp | - |
| pvAnimateDuration  | Animation duration  | integer | 1500 | - |

# Home To Install
```
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```
```
implementation 'com.shimaami.android:kotlinprogressview:1.0.0'
```

# Home To Use
### XML
```
 <com.progress.kotlingprogressview.ProgressView
            android:id="@+id/progressCircle"
            android:layout_width="150dp"
            android:layout_height="150dp"
            android:layout_marginBottom="10dp"
            android:layout_gravity="center_horizontal"
            app:pvDirection="fromLeft"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:pvProgress="0.5"
            app:pvShape="circle"/>
```
### Apply gradient effect
```
progressCircle.applyGradient(Color.GREEN, Color.YELLOW, Color.RED)

```
