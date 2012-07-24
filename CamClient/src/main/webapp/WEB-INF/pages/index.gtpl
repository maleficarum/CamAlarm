<% include '/WEB-INF/includes/header.gtpl' %>

<%
    def currentCam = request.getAttribute("currentCam")
%>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span4">
            <div class="well sidebar-nav">
                <ul class="nav nav-list">
                    <li class="nav-header">Camaras disponibles</li>
                    <% application.getAttribute("cams").each { cam -> %>
                        <li><a href="/verDetalle?camname=${cam.name}">${cam.name}</a></li>
                    <% } %>

                </ul>
            </div><!--/.well -->
        </div><!--/span-->
        <div class="span4">
            <div class="well sidebar-nav">
                <% if(currentCam) { %>
                    <h4>Detalle</h4>
                    <ul>
                        <li>Nombre : ${currentCam.name}</li>
                        <li>Ultimo contacto : ${currentCam.lastPingDate}</li>
                        <li>Fecha de registro : ${currentCam.loginDate}</li>
                        <li>Direccion IP : ${currentCam.ip}</li>
                    </ul>
                <% } %>
            </div><!--/.well -->
        </div><!--/span-->
        <div class="span4">
            <div class="well sidebar-nav">

            </div><!--/.well -->
        </div><!--/span-->
    </div>
</div>

<% include '/WEB-INF/includes/footer.gtpl' %>

