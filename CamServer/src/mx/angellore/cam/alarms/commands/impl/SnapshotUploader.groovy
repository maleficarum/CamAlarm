package mx.angellore.cam.alarms.commands.impl

import groovyx.net.http.RESTClient
import org.apache.http.entity.FileEntity

/**
 * Date: 27/07/12 15:36
 * @author Oscar I. Hernandez
 */

@Grapes ([ @Grab("org.apache.httpcomponents:httpcore:4.2"), @Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.5.2")])
class SnapshotUploader {

    def file = new java.io.File("//Users/angellore/Desktop/acta_nacimiento.jpg")

    def upload() {
        def rest = new RESTClient( 'http://localhost:8080/postImage' )
        rest.encoder.'image/jpeg' = this.&encodeZipFile
        rest.post( path:'test/test.jpeg', body: file, requestContentType: 'image/jpeg', {
            println("RESPEUESTA " + it.getStatus())
        } )
    }

    def encodeZipFile( data ) throws UnsupportedEncodingException {
        if ( data instanceof File ) {
            def entity = new FileEntity( (File) data, "application/zip" );
            entity.setContentType( "application/zip" );
            return entity
        } else {
            throw new IllegalArgumentException(
                    "Don't know how to encode ${data.class.name} as a zip file" );
        }
    }

    static void main(def args) {
        new SnapshotUploader().upload()
    }


}
