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

    private static Tuple2<LicensesTask, File> generate(Project project, String variant) {

        def getDependencies = project.tasks.create("get${variant.capitalize()}Dependencies", DependencyTask)
        def dependencyOutput = new File(project.buildDir, "generated/third_party_licenses")
        def generatedJson = new File(dependencyOutput, "dependencies.json")
        getDependencies.setOutputFile(generatedJson)
        getDependencies.configurations = project.getConfigurations()
        getDependencies.setVariant(variant)

        def resourceOutput = new File(dependencyOutput, "/res")
        def outputDir = new File(resourceOutput, "/raw")
        def licensesFile = new File(outputDir, "third_party_licenses.html")

        def licenseTask = project.tasks.create("generate${variant.capitalize()}Licenses", LicensesTask)

        licenseTask.dependenciesJson = generatedJson
        licenseTask.html = licensesFile

        licenseTask.inputs.file(generatedJson)
        licenseTask.outputs.dir(outputDir)
        licenseTask.outputs.files(licensesFile)

        licenseTask.dependsOn(getDependencies)

        [licenseTask, resourceOutput]
    }

    void apply(Project project) {

        project.android.applicationVariants.all { BaseVariant variant ->

            def (LicensesTask licenseTask, File resourceOutput) = generate(project, variant.name)

            variant.preBuildProvider.configure { dependsOn(licenseTask) }

            def generated = project.files(resourceOutput).builtBy(licenseTask)
            variant.registerGeneratedResFolders(generated)
            variant.mergeResourcesProvider.configure { dependsOn(licenseTask) }
        }
    }
}