<script type="text/javascript">
	function autoPopulate_${parameters.escapedId?html}(targetElement) {	
		<#if parameters.headerKey?exists && parameters.headerValue?exists>
		if (targetElement.options[targetElement.selectedIndex].value == '${parameters.headerKey?html}') {
			return;
		}			
		</#if>
		<#if parameters.emptyOption?default(false)>
		if (targetElement.options[targetElement.selectedIndex].value == '') {
		    return;
		}
		</#if>
		targetElement.form.elements['${parameters.name?html}'].value=targetElement.options[targetElement.selectedIndex].value;
	}
</script>
<#include "/${parameters.templateDir}/simple/textHDIV.ftl" />
<br/>
<#if parameters.list?exists>
<select onChange="autoPopulate_${parameters.escapedId?html}(this);"<#rt/>
    <#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
    </#if>
>
	<#if (parameters.headerKey?exists && parameters.headerValue?exists)>
		<option value="${parameters.headerKey?html}">${parameters.headerValue?html}</option>
	</#if>
	<#if parameters.emptyOption?default(false)>
		<#assign hdivValue = dataComposer.compose("${parameters.name?html}", "", false)/><#rt/>
	    <option value="${hdivValue}"></option>	  
	</#if>
    <@s.iterator value="parameters.list">
    <#if parameters.listKey?exists>
    	<#assign tmpListKey = stack.findString(parameters.listKey) />
    <#else>
    	<#assign tmpListKey = stack.findString('top') />
    </#if>
    <#if parameters.listValue?exists>
    	<#assign tmpListValue = stack.findString(parameters.listValue) />
    <#else>
    	<#assign tmpListValue = stack.findString('top') />
    </#if>
	<#assign hdivValue = dataComposer.compose("${parameters.name?html}", tmpListKey, false) />
    <option value="${hdivValue}"<#rt/>
        <#if (parameters.nameValue == tmpListKey)>
 selected="selected"<#rt/>
        </#if>
    ><#t/>
            ${tmpListValue?html}<#t/>
    </option><#lt/>
    </@s.iterator>
</select>
</#if>
