/*
 * Copyright (C) 2017-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.analyzer.managers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

import java.io.File

import org.ossreviewtoolkit.analyzer.Analyzer
import org.ossreviewtoolkit.downloader.vcs.Git
import org.ossreviewtoolkit.model.yamlMapper
import org.ossreviewtoolkit.utils.Ci
import org.ossreviewtoolkit.utils.test.DEFAULT_ANALYZER_CONFIGURATION
import org.ossreviewtoolkit.utils.test.patchActualResult
import org.ossreviewtoolkit.utils.test.patchExpectedResult

class SbtTest : StringSpec({
    "Dependencies of the single 'directories' project should be detected correctly".config(
        // Disabled on Azure Windows because it fails for unknown reasons.
        enabled = !Ci.isAzureWindows
    ) {
        val projectName = "directories"
        val projectDir = File("src/funTest/assets/projects/external/$projectName").absoluteFile
        val expectedOutputFile = projectDir.resolveSibling("$projectName-expected-output.yml")

        // Clean any previously generated POM files / target directories.
        Git().run(projectDir, "clean", "-fd")

        val ortResult = Analyzer(DEFAULT_ANALYZER_CONFIGURATION).analyze(projectDir, listOf(Sbt.Factory()))

        val actualResult = yamlMapper.writeValueAsString(ortResult)
        val expectedResult = patchExpectedResult(expectedOutputFile)

        patchActualResult(actualResult, patchStartAndEndTime = true) shouldBe expectedResult
    }

    "Dependencies of the 'sbt-multi-project-example' multi-project should be detected correctly".config(
        // Disabled on Azure Windows because it fails for unknown reasons.
        enabled = !Ci.isAzureWindows
    ) {
        val projectName = "sbt-multi-project-example"
        val projectDir = File("src/funTest/assets/projects/external/$projectName").absoluteFile
        val expectedOutputFile = File(projectDir.parentFile, "$projectName-expected-output.yml")

        // Clean any previously generated POM files / target directories.
        Git().run(projectDir, "clean", "-fd")

        val ortResult = Analyzer(DEFAULT_ANALYZER_CONFIGURATION).analyze(projectDir, listOf(Sbt.Factory()))

        val actualResult = yamlMapper.writeValueAsString(ortResult)
        val expectedResult = patchExpectedResult(expectedOutputFile)

        patchActualResult(actualResult, patchStartAndEndTime = true) shouldBe expectedResult
    }
})
