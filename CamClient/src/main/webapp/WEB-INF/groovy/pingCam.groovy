import domain.Cam

def camname = params['camname']
def cams = application.getAttribute("cams")

if(!cams) {
    cams = []
}


def cam = cams.find { it.name == camname }

if(!cam) {
    cam = new Cam()
    cam.name = camname
    cam.loginDate = new Date()
}

cam.lastPingDate = new Date()
cam.ip = request.getRemoteAddr()

cams.remove(cam)
cams << cam
application.setAttribute("cams", cams)

forward '/WEB-INF/pages/pingCam.gtpl'