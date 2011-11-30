/*--

 Copyright (C) 2002-2003 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The names "OBE" and "Open Business Engine" must not be used to
    endorse or promote products derived from this software without prior
    written permission.  For written permission, please contact
    me@anthonyeden.com.

 4. Products derived from this software may not be called "OBE" or
    "Open Business Engine", nor may "OBE" or "Open Business Engine"
    appear in their name, without prior written permission from
    Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on OBE, please see <http://www.openbusinessengine.org/>.

 */
package org.fireflow.model.io;

import org.dom4j.Namespace;
/**
 * @author Anthony Eden
 * updated by nychen2000
 */
public interface FPDLNames {
    /** Namespace prefix to use for XSD elements. */
    String XSD_NS_PREFIX = "xsd";
    
    /** The XSD namespace URI. */
    String XSD_URI = "http://www.w3.org/2001/XMLSchema";
    
    /** Namespace prefix to use for XSD elements. */
    String XSI_NS_PREFIX = "xsi";
    /** The XSD namespace URI. */
    String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    
    String XSI_SCHEMA_LOCATION = "schemaLocation";
    
    /** Namespace prefix to use for FPDL elements. */
    String FPDL_NS_PREFIX = "fpdl";
    
    /** The XPDL namespace URI. */
    String FPDL_URI = "http://www.fireflow.org/Fireflow_Process_Definition_Language";
    
    String PUBLIC_ID = "-//Nieyun Chen//ProcessDefinition//CN";
    
    String SYSTEM_ID = "FireFlow_Process_Definition_Language.dtd";
    
    /** The FPDL schema URI. */
    String FPDL_SCHEMA_LOCATION =
        "http://www.fireflow.org/2007/fpdl1.0 fpdl-1.0.xsd";
    
    /** Unique identifier. */
    String ID = "Id";

    /** Entity name. */
    String NAME = "Name";

    /** Tag which defines a brief description of an element. */
    String DESCRIPTION = "Description";
    
    String DISPLAY_NAME="DisplayName";

    String EXTENDED_ATTRIBUTES = "ExtendedAttributes";
    String EXTENDED_ATTRIBUTE = "ExtendedAttribute";
    String VALUE = "Value";

    String VERSION = "Version";

//    String PARTICIPANTS = "Participants";
//    String PARTICIPANT = "Participant";
//    String PARTICIPANT_TYPE = "ParticipantType";
    String ASSIGNMENT_HANDLER = "AssignmentHandler";


//    String EVENT_TYPES = "EventTypes";
//    String EVENT_TYPE = "EventType";
//    String EVENT = "Event";

//    String APPLICATIONS = "Applications";
    String APPLICATION = "Application";
    String HANDLER = "Handler";

//    String WORKFLOW_PROCESSES = "WorkflowProcesses";
    String WORKFLOW_PROCESS = "WorkflowProcess";


    String PRIORITY = "Priority";

    String DURATION = "Duration";

    String START_NODE = "StartNode";
    String END_NODE = "EndNode";
    String END_NODES = "EndNodes";

    String ACTIVITIES = "Activities";
    String ACTIVITY = "Activity";
    

    String SYNCHRONIZERS = "Synchronizers";
    String SYNCHRONIZER = "Synchronizer";

    String TASKS = "Tasks";
    String TASK = "Task";


    String TASKREFS = "TaskRefs";
    String TASKREF = "TaskRef";
    String REFERENCE = "Reference";
    
    String SUBFLOW = "SubFlow";
    
    String DATA_FIELDS = "DataFields";
    String DATA_FIELD = "DataField";
    String INITIAL_VALUE = "InitialValue";
    String LENGTH = "Length";

    String PERFORMER = "Performer";
    String START_MODE = "StartMode";
    String FINISH_MODE = "FinishMode";
    String MANUAL = "Manual";
    String AUTOMATIC = "Automatic";

    String TRANSITIONS = "Transitions";
    String TRANSITION = "Transition";
    String FROM = "From";
    String TO = "To";

    String LOOPS = "Loops";
    String LOOP = "Loop";
    String LOOP_STRATEGY = "LoopStrategy";


    String CONDITION = "Condition";

    String TYPE = "Type";
    String DATA_TYPE = "DataType";


    String NAMESPACE = "namespace";

    String EXCEPTION_NAME = "ExceptionName";

    String RESOURCE_FILE = "ResourceFile";
    String RESOURCE_MANAGER = "ResourceManager";
    
    String COMPLETION_STRATEGY = "CompletionStrategy";
    String DEFAULT_VIEW = "DefaultView";
    String EXECUTION = "Execution";
    String EDIT_FORM = "EditForm";
    String VIEW_FORM = "ViewForm";
    String LIST_FORM = "ListForm";
    String URI = "Uri";
    String UNIT = "Unit";
    String IS_BUSINESS_TIME = "IsBusinessTime";
    
    String SUB_WORKFLOW_PROCESS = "SubWorkflowProcess";
    String WORKFLOW_PROCESS_ID = "WorkflowProcessId";
    
    String EVENT_LISTENERS = "EventListeners";
    String EVENT_LISTENER = "EventListener";
    String CLASS_NAME = "ClassName";

    String TASK_INSTANCE_CREATOR = "TaskInstanceCreator";
    String TASK_INSTANCE_RUNNER = "TaskInstanceRunner";
    String TASK_INSTANCE_COMPLETION_EVALUATOR = "TaskInstanceCompletionEvaluator";

    String FORM_TASK_INSTANCE_RUNNER = "FormTaskInstanceRunner";
    String TOOL_TASK_INSTANCE_RUNNER = "ToolTaskInstanceRunner";
    String SUBFLOW_TASK_INSTANCE_RUNNER = "SubflowTaskInstanceRunner";

    String FORM_TASK_INSTANCE_COMPLETION_EVALUATOR = "FormTaskInstanceCompletionEvaluator";
    String TOOL_TASK_INSTANCE_COMPLETION_EVALUATOR = "ToolTaskInstanceCompletionEvaluator";
    String SUBFLOW_TASK_INSTANCE_COMPLETION_EVALUATOR = "SubflowTaskInstanceCompletionEvaluator";

    Namespace XSD_NS = new Namespace(XSD_NS_PREFIX, XSD_URI);
    Namespace XSI_NS = new Namespace(XSI_NS_PREFIX, XSI_URI);
    Namespace FPDL_NS = new Namespace(FPDL_NS_PREFIX, FPDL_URI);

    
}
