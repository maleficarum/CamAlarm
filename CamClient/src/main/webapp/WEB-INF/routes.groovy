
get "/", forward: "/WEB-INF/pages/index.gtpl"
get "/pingCam", forward: "/pingCam.groovy"
get "/verDetalle", forward: "/verDetalle.groovy"
post '/postImage', forward:  "/postImage.groovy"
get '/postImage', forward:  "/postImage.groovy"

get "/favicon.ico", redirect: "/images/gaelyk-small-favicon.png"
jabber to: "/receiveJabber.groovy"
