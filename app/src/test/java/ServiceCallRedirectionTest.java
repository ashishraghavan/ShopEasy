import com.google.common.collect.ImmutableList;
import com.shopping.shopeasy.network.Response;
import com.shopping.shopeasy.network.ServiceCall;

import org.apache.http.message.BasicHeader;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ServiceCallRedirectionTest {

    @Test
    public void testLinkedInRedirectionTest() throws Exception {
        final List<BasicHeader> basicHeaderList = ImmutableList.<BasicHeader>builder().add(new BasicHeader("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36")).build();
        final ServiceCall linkedServiceCall = new ServiceCall.ServiceCallBuilder()
                .setUrl("https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=75zomdyjvbmlu2" +
                        "&redirect_uri=https://linkedin.com/success&state=987654321&scope=r_basicprofile")
                .setMethod(ServiceCall.EMethodType.GET)
                .setHeaderElements(basicHeaderList)
                .shouldFollowRedirects(false)
                .overrideCache(true)
                .build();
        final Response serviceCallResponse = linkedServiceCall.executeRequest();
        System.out.println(serviceCallResponse.getConvertedEntity());
        final LinkedList<String> redirectionUrlList =linkedServiceCall.getUrlLinkedList();
        if ( redirectionUrlList != null ) {
            for ( String link : redirectionUrlList ) {
                System.out.println(link);
            }
        }
    }

}
