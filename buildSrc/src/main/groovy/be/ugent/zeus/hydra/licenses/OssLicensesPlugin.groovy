/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ugent.zeus.hydra.licenses

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class OssLicensesPlugin implements Plugin<Project> {

    private static generate(Project project, String variant) {

        def getDependencies = project.tasks.create("get${variant.capitalize()}Dependencies", DependencyTask)
        def dependencyOutput = new File(project.buildDir, "generated/third_party_licenses")
        def generatedJson = new File(dependencyOutput, "dependencies.json")
        getDependencies.configurations = project.getConfigurations()
        getDependencies.outputDir = dependencyOutput
        getDependencies.outputFile = generatedJson
        getDependencies.variant = variant

        def resourceOutput = new File(dependencyOutput, "/res")
        def outputDir = new File(resourceOutput, "/raw")
        def licensesFile = new File(outputDir, "third_party_licenses.html")

        def licenseTask = project.tasks.create("generate${variant.capitalize()}Licenses", LicensesTask)

        licenseTask.dependenciesJson = generatedJson
        licenseTask.outputDir = outputDir
        licenseTask.html = licensesFile

        licenseTask.inputs.file(generatedJson)
        licenseTask.outputs.dir(outputDir)
        licenseTask.outputs.files(licensesFile)

        licenseTask.dependsOn(getDependencies)


        def cleanupTask = project.tasks.create("${variant}LicensesCleanUp", LicensesCleanUpTask)
        cleanupTask.dependencyFile = generatedJson
        cleanupTask.dependencyDir = dependencyOutput
        cleanupTask.htmlFile = licensesFile
        cleanupTask.licensesDir = outputDir

        project.tasks.findByName("clean").dependsOn(cleanupTask)

        [licenseTask, resourceOutput]
    }

    void apply(Project project) {

        project.android.applicationVariants.all { BaseVariant variant ->

            def (licenseTask, resourceOutput) = generate(project, variant.name)

            // This is necessary for backwards compatibility with versions of gradle that do not support
            // this new API.
            if (variant.hasProperty("preBuildProvider")) {
                variant.preBuildProvider.configure { dependsOn(licenseTask) }
            } else {
                //noinspection GrDeprecatedAPIUsage
                variant.preBuild.dependsOn(licenseTask)
            }

            // This is necessary for backwards compatibility with versions of gradle that do not support
            // this new API.
            if (variant.respondsTo("registerGeneratedResFolders")) {
                licenseTask.ext.generatedResFolders = project.files(resourceOutput).builtBy(licenseTask)
                variant.registerGeneratedResFolders(licenseTask.generatedResFolders)

                if (variant.hasProperty("mergeResourcesProvider")) {
                    variant.mergeResourcesProvider.configure { dependsOn(licenseTask) }
                } else {
                    //noinspection GrDeprecatedAPIUsage
                    variant.mergeResources.dependsOn(licenseTask)
                }
            } else {
                //noinspection GrDeprecatedAPIUsage
                variant.registerResGeneratingTask(licenseTask, resourceOutput)
            }
        }
    }
}