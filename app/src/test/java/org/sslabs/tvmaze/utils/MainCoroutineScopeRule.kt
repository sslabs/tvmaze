/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sslabs.tvmaze.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * MainCoroutineScopeRule installs a TestDispatcher for Dispatchers.Main.
 *
 * You may call [advanceUntilIdle] on [MainCoroutineScopeRule] to control the virtual-clock.
 *
 * @param dispatcher if provided, this [TestDispatcher] will be used.
 */
@ExperimentalCoroutinesApi
class MainCoroutineScopeRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher(), CoroutineScope by TestScope(dispatcher) {

    private val testScope = TestScope(dispatcher)

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    fun advanceUntilIdle() {
        testScope.advanceUntilIdle()
    }
}
