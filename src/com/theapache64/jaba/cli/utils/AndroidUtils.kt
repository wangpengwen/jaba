package com.theapache64.jaba.cli.utils

import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory

class AndroidUtils(private val projectDir: String) {

    companion object {
        private const val KOTLIN_PLUGIN = "    id 'kotlin-android'"
    }

    // Common files
    val gradleFile: File = File("$projectDir/app/build.gradle")
    val projectGradleFile = File("$projectDir/build.gradle")

    // Common properties
    private var packageName: String? = null
    private var projectName: String? = null
    private var rootSrcPath: String? = null

    val mainLayoutFile = File("$projectDir/app/src/main/res/layout/activity_main.xml")
    val logInLayoutFile = File("$projectDir/app/src/main/res/layout/activity_log_in.xml")
    val contentMainLayoutFile = File("$projectDir/app/src/main/res/layout/content_main.xml")
    val manifestFile = File("$projectDir/app/src/main/AndroidManifest.xml")
    val oldMainActivityFile = File("${provideRootSourcePath()}/MainActivity.kt")
    val mainActivityFile = File("${provideRootSourcePath()}/ui/activities/main/MainActivity.kt")
    val appFile = File("${provideRootSourcePath()}/App.kt")
    val mainViewModelFile = File("${provideRootSourcePath()}/ui/activities/main/MainViewModel.kt")
    val mainHandlerFile = File("${provideRootSourcePath()}/ui/activities/main/MainHandler.kt")
    val activityBuilderModuleFile = File("${provideRootSourcePath()}/di/modules/ActivitiesBuilderModule.kt")
    val appComponentFile = File("${provideRootSourcePath()}/di/components/AppComponent.kt")
    val logInRequestFile = File("${provideRootSourcePath()}/data/remote/login/LogInRequest.kt")
    val logInResponseFile = File("${provideRootSourcePath()}/data/remote/login/LogInResponse.kt")
    val userRepoFile = File("${provideRootSourcePath()}/data/repositories/UserPrefRepository.kt")

    // Database
    val appDatabaseFile = File("${provideRootSourcePath()}/data/local/AppDatabase.kt")
    val sampleDaoFile = File("${provideRootSourcePath()}/data/local/daos/SampleDao.kt")
    val sampleEntityFile = File("${provideRootSourcePath()}/data/local/entities/SampleEntity.kt")
    val databaseModuleFile = File("${provideRootSourcePath()}/di/modules/DatabaseModule.kt")
    val projectGradlePropertiesFile = File("$projectDir/gradle.properties")

    val appModuleFile = File("${provideRootSourcePath()}/di/modules/AppModule.kt")
    val networkModuleFile = File("${provideRootSourcePath()}/di/modules/NetworkModule.kt")
    val viewModelModuleFile = File("${provideRootSourcePath()}/di/modules/ViewModelModule.kt")
    val apiInterfaceFile = File("${provideRootSourcePath()}/data/remote/ApiInterface.kt")
    val splashViewModelFile = File("${provideRootSourcePath()}/ui/activities/splash/SplashViewModel.kt")
    val splashActivityFile = File("${provideRootSourcePath()}/ui/activities/splash/SplashActivity.kt")
    val splashActivityLayoutFile = File("$projectDir/app/src/main/res/layout/activity_splash.xml")
    val stylesFile = File("$projectDir/app/src/main/res/values/styles.xml")
    val stylesNightFile = File("$projectDir/app/src/main/res/values-night/styles.xml")
    val stringXmlFile = File("$projectDir/app/src/main/res/values/strings.xml")
    val colorsFile = File("$projectDir/app/src/main/res/values/colors.xml")
    val dimensFile = File("$projectDir/app/src/main/res/values/dimens.xml")
    val menuMainFile = File("$projectDir/app/src/main/res/menu/menu_main.xml")
    val androidIcon = File("$projectDir/app/src/main/res/drawable/ic_android.png")
    val logInActivityFile = File("${provideRootSourcePath()}/ui/activities/login/LogInActivity.kt")
    val logInViewModelFile = File("${provideRootSourcePath()}/ui/activities/login/LogInViewModel.kt")
    val logInHandler = File("${provideRootSourcePath()}/ui/activities/login/LogInHandler.kt")
    val authRepositoryFile = File("${provideRootSourcePath()}/data/repositories/AuthRepository.kt")
    val userIconFile = File("$projectDir/app/src/main/res/drawable/ic_user.png")
    val logOutIcon = File("$projectDir/app/src/main/res/drawable/ic_logout_white.xml")

    /**
     * Return true if it's an android project.
     */
    fun isAndroidProject(): Boolean {
        return gradleFile.exists()
    }

    fun getLayoutFile(layoutFileName: String): File {
        return File("$projectDir/app/src/main/res/layout/$layoutFileName")
    }


    /**
     * To get package name from app/app.build.gradle
     */
    fun providePackageName(): String {

        if (packageName == null) {
            try {
                packageName = getWithDoubleQuotes("applicationId")
            } catch (e: IndexOutOfBoundsException) {
                throw IllegalArgumentException("Couldn't find package name from gradle file")
            }
        }

        return packageName as String
    }

    fun provideRootSourcePath(): String {
        val packageName = providePackageName()
        try {
            if (rootSrcPath == null) {
                rootSrcPath = "$projectDir/app/src/main/java/${packageName.replace('.', '/')}"
            }
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Failed to get root source path")
        }
        return rootSrcPath!!
    }

    private fun getWithDoubleQuotes(delimiter: String) =
        gradleFile.readText().split("$delimiter \"")[1].split("\"")[0]


    /**
     * To return kotlin version from project/app.build.gradle
     */
    fun getKotlinVersion(): String {
        val readText = projectGradleFile.readText()
        try {
            return readText.split("ext.kotlin_version = '")[1].split("'")[0]
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Couldn't get kotlin version")
        }
    }

    /**
     * To getWithDoubleQuotes compileSdkVersion, minSdkVersion and targetSdkVersion in a Triple object with respective position
     */
    fun getGradleVars(): Triple<String, String, String> {
        try {
            val fileContent = gradleFile.readText()
            val compileSdkVersion = fileContent.split("compileSdkVersion ")[1].split("\n")[0]
            val minSdkVersion = fileContent.split("minSdkVersion ")[1].split("\n")[0]
            val targetSdkVersion = fileContent.split("targetSdkVersion ")[1].split("\n")[0]
            return Triple(compileSdkVersion, minSdkVersion, targetSdkVersion)
        } catch (e: IndexOutOfBoundsException) {
            throw java.lang.IllegalArgumentException("Couldn't get gradle vars from gradle file")
        }
    }

    /**
     * Return project name from strings.xml
     */
    fun provideProjectName(): String {

        if (projectName == null) {
            val stringsFile = File("$projectDir/app/src/main/res/values/strings.xml")

            // parse XML
            val docBuilderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docBuilderFactory.newDocumentBuilder()
            val doc = docBuilder.parse(stringsFile)
            val root = doc.documentElement
            val strings = root.getElementsByTagName("string")
            for (i in 0 until strings.length) {
                val stringNode = strings.item(i) as org.w3c.dom.Element
                if (stringNode.getAttribute("name") == "app_name") {
                    this.projectName = stringNode.firstChild.nodeValue
                    break
                }
            }

            if (projectName == null) {
                throw IOException("Couldn't get project name from strings")
            }
        }

        return projectName as String
    }


    /**
     * Returns true if the project is a kotlin android project.
     */
    fun isKotlinProject(): Boolean {

        val gradleFileContents = gradleFile.readText()
        val mainProPath = providePackageName().replace(".", "/")
        val mainActPath = "$projectDir/app/src/main/java/$mainProPath/MainActivity.kt"

        return gradleFileContents.contains(KOTLIN_PLUGIN) && File(mainActPath).exists()
    }

    fun getSomeActivityFile(): String {
        return ""
    }
}