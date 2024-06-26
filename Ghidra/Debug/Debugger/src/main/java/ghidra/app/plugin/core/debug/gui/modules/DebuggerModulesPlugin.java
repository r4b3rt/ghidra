/* ###
 * IP: GHIDRA
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
package ghidra.app.plugin.core.debug.gui.modules;

import ghidra.app.events.*;
import ghidra.app.plugin.PluginCategoryNames;
import ghidra.app.plugin.core.debug.AbstractDebuggerPlugin;
import ghidra.app.plugin.core.debug.DebuggerPluginPackage;
import ghidra.app.plugin.core.debug.event.TraceActivatedPluginEvent;
import ghidra.app.plugin.core.debug.event.TraceClosedPluginEvent;
import ghidra.app.services.*;
import ghidra.framework.options.SaveState;
import ghidra.framework.plugintool.*;
import ghidra.framework.plugintool.util.PluginStatus;

@PluginInfo(
	shortDescription = "Debugger module and section manager",
	description = "GUI to manage modules and sections",
	category = PluginCategoryNames.DEBUGGER,
	packageName = DebuggerPluginPackage.NAME,
	status = PluginStatus.RELEASED,
	eventsConsumed = {
		ProgramOpenedPluginEvent.class,
		ProgramActivatedPluginEvent.class,
		ProgramLocationPluginEvent.class,
		ProgramClosedPluginEvent.class,
		TraceActivatedPluginEvent.class,
		TraceClosedPluginEvent.class,
	},
	servicesRequired = {
		DebuggerStaticMappingService.class,
		DebuggerTraceManagerService.class,
		ProgramManager.class,
	})
public class DebuggerModulesPlugin extends AbstractDebuggerPlugin {
	protected DebuggerModulesProvider provider;

	public DebuggerModulesPlugin(PluginTool tool) {
		super(tool);
	}

	@Override
	protected void init() {
		provider = new DebuggerModulesProvider(this);
		super.init();
	}

	@Override
	protected void dispose() {
		provider.dispose();
		tool.removeComponentProvider(provider);
		super.dispose();
	}

	@Override
	public void processEvent(PluginEvent event) {
		super.processEvent(event);
		if (event instanceof ProgramOpenedPluginEvent ev) {
			provider.programOpened(ev.getProgram());
		}
		else if (event instanceof ProgramActivatedPluginEvent ev) {
			provider.setProgram(ev.getActiveProgram());
		}
		else if (event instanceof ProgramLocationPluginEvent ev) {
			provider.setLocation(ev.getLocation());
		}
		else if (event instanceof ProgramClosedPluginEvent ev) {
			provider.programClosed(ev.getProgram());
		}
		else if (event instanceof TraceActivatedPluginEvent ev) {
			provider.coordinatesActivated(ev.getActiveCoordinates());
		}
		else if (event instanceof TraceClosedPluginEvent ev) {
			provider.traceClosed(ev.getTrace());
		}
	}

	@Override
	public void readConfigState(SaveState saveState) {
		provider.readConfigState(saveState);
	}

	@Override
	public void writeConfigState(SaveState saveState) {
		provider.writeConfigState(saveState);
	}
}
