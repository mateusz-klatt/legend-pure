// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.pure.runtime.java.interpreted;

import org.finos.legend.pure.runtime.java.shared.listeners.ExecutionEndListener;
import org.finos.legend.pure.runtime.java.shared.listeners.ExecutionListeners;
import org.finos.legend.pure.runtime.java.shared.listeners.IdentifiableExecutionEndListener;

public class ExecutionSupport implements org.finos.legend.pure.m3.execution.ExecutionSupport
{
    private final ExecutionListeners executionListeners = new ExecutionListeners();

    public void registerExecutionEndListener(ExecutionEndListener executionEndListener)
    {
        this.executionListeners.registerExecutionEndListener(executionEndListener);
    }

    @Deprecated
    public void registerIdentifableExecutionEndListener(IdentifiableExecutionEndListener identifiableExecutionEndListener)
    {
        registerIdentifiableExecutionEndListener(identifiableExecutionEndListener);
    }

    public void registerIdentifiableExecutionEndListener(IdentifiableExecutionEndListener identifiableExecutionEndListener)
    {
        this.executionListeners.registerIdentifiableExecutionEndListener(identifiableExecutionEndListener);
    }

    @Deprecated
    public void unRegisterIdentifableExecutionEndListener(String listenerId)
    {
        unRegisterIdentifiableExecutionEndListener(listenerId);
    }

    public void unRegisterIdentifiableExecutionEndListener(String listenerId)
    {
        this.executionListeners.unRegisterIdentifiableExecutionEndListener(listenerId);
    }

    public void executionEnd(final Exception exception)
    {
        this.executionListeners.executionEnd(exception);
    }
}
