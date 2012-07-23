def message = xmpp.parseMessage(request)

log.info "Received from ${message.from} with body ${message.body}"