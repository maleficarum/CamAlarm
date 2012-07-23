import domain.Cam

def camname = params['camname']
def cams = application.getAttribute("cams")

if(!cams) {
    cams = []
}


def cam = cams.find { it.name == camname }

if(!cam) {
    cam = new Cam()
    cam.lastPingDate = new Date()
    cam.name = camname
    cam.loginDate = new Date()
} else {
    cam.lastPingDate = new Date()
}
cams.remove(cam)
cams << cam
application.setAttribute("cams", cams)

forward '/WEB-INF/pages/index.gtpl'