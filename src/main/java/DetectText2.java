// Imports the Google Cloud client library

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageContext;
import com.google.protobuf.ByteString;

public class DetectText2 {
  public static void main(String... args) throws Exception {
    // Instantiates a client
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

    	
      // The path to the image file to annotate
      String fileName = "./resources/ssc.png";
   // Reads the image file into memory
      Path path = Paths.get(fileName);
      byte[] data = Files.readAllBytes(path);
      //byte[] imageData = Base64.encodeBase64(Files.readAllBytes(path));
      ByteString imgBytes = ByteString.copyFrom(data);

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();
      detectTextGcs(img, System.out);
    }
  }
  public static void detectTextGcs(Image img, PrintStream out) throws Exception, IOException {
	  List<AnnotateImageRequest> requests = new ArrayList<>();

	  //ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
	  //Image img = Image.newBuilder().setSource(imgSource).build();
	  Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
	  List<String> values = new ArrayList<String>();
	  values.add("en");
	  values.add("hi");
	 ImageContext imageContext = ImageContext.newBuilder().addAllLanguageHints(values).build();
	 
	  AnnotateImageRequest request =
	      AnnotateImageRequest.newBuilder().addFeatures(feat)
	      .setImage(img)
	      //.setImageContext(imageContext)
	      .build();
	  requests.add(request);

	  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
	    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	    List<AnnotateImageResponse> responses = response.getResponsesList();

	    for (AnnotateImageResponse res : responses) {
	      if (res.hasError()) {
	        out.printf("Error: %s\n", res.getError().getMessage());
	        return;
	      }
	      //System.out.println("hello--->"+res.getFullTextAnnotation().getText());;
	      // For full list of available annotations, see http://g.co/cloud/vision/docs
	      for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
	    	  //System.out.println(annotation.getAllFields());
	    	  //System.out.println(annotation.getDescriptionBytes().toString());
	        out.printf("Text: %s\n", annotation.getDescription());
	        out.printf("Position : %s\n", annotation.getBoundingPoly());
	      }
	    
	  }
	}
  }
}