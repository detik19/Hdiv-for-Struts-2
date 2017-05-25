<#assign hdivValue = dataComposer.compose(parameters.tokenNameField?default(""), parameters.name?default("")?html, false)/>
<input type="hidden" name="${parameters.tokenNameField?default("")}" value="${hdivValue}"/>
<#assign hdivValue = dataComposer.compose(parameters.name?default(""), parameters.token?default("")?html, false)/>
<input type="hidden" name="${parameters.name?default("")}" value="${hdivValue}"/>
