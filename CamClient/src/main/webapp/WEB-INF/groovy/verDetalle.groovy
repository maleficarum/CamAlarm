def cams = application.getAttribute("cams")
def camname = params['camname']
def cam = cams.find { it.name == camname }

request.setAttribute("currentCam", cam)

forward '/WEB-INF/pages/index.gtpl'