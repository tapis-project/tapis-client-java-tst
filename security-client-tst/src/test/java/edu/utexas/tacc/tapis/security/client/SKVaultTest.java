package edu.utexas.tacc.tapis.security.client;

import org.testng.annotations.Test;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.security.client.gen.model.SkSecretVersionMetadata;
import edu.utexas.tacc.tapis.security.client.model.SKSecretMetaParms;
import edu.utexas.tacc.tapis.security.client.model.SecretType;

@Test(groups={"integration"})
public class SKVaultTest 
{
    /* ********************************************************************** */
    /*                               Constants                                */
    /* ********************************************************************** */
    // A hardcoded JWT with a 10 year expiration.  Here's the curl command that 
    // created the JWT:
    //
    //  curl -H "Content-type: application/json" -d '{"token_tenant_id": "dev", "token_type": "user", "token_username": "testuser2", "access_token_ttl": 315569520}'  'https://dev.develop.tapis.io/tokens'
    //
    private static final String JWT =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoidGVzdHVzZXIyQGRldiIsInRhcGlzL3RlbmFudF9pZCI6ImRldiIsInRhcGlzL3Rva2VuX3R5cGUiOiJhY2Nlc3MiLCJ0YXBpcy9kZWxlZ2F0aW9uIjpmYWxzZSwidGFwaXMvZGVsZWdhdGlvbl9zdWIiOm51bGwsInRhcGlzL3VzZXJuYW1lIjoidGVzdHVzZXIyIiwidGFwaXMvYWNjb3VudF90eXBlIjoidXNlciIsImV4cCI6MTg4ODU1Njc1MX0.sC7uZAUpWYqiSuVEUWbHr6nWhHY2BCLQ0gBHCytaeGBPAbGb51g-vT2M_Y_r4JvEXD6m4HSYPmn3uHIkPYCK4JohNhTcq1iq-C4o-hnwS4MehQBm29-YHnMAOyPAaKqW8Uxt9rfbRwsLcfmQqG8U3BuasF4FzAQQ1PH8myMSG0BmaOEXOmx6tcrhlwGJ5HUCARRK1lUamKxmScj1QuGvn8Wljexv8ne0MdR4KscaCAyVTDBTOMzMPTH06G8ScZ14ecOZSBLnknI7fK1PWE39kEO9-eBln2uWpOHIg2twRPOZuy-lHic7aIBFzJhGI_J-ie_VBSzYTryNExGgGV9H1g";
    
    /* ---------------------------------------------------------------------- */
    /* testRole:                                                              */
    /* ---------------------------------------------------------------------- */
    @Test(enabled = false)
    public void testRole() throws TapisClientException
    {
        SKClient skClient = new SKClient(null, JWT);
        
        // Read an existing secret's metadata.
        SKSecretMetaParms parms = new SKSecretMetaParms(SecretType.System)
                                  .setSecretName("secret1")
                                  .setSysId("sys1")
                                  .setSysUser("bud");
        SkSecretVersionMetadata resp = skClient.readSecretMeta(parms);
        System.out.println("secretReadMeta: " + resp + "\n");
        
    }
}
