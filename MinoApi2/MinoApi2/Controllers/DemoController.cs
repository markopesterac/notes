using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Minio;
using Minio.DataModel;
using Minio.Exceptions;
using System.Threading.Tasks;
using System.Web.Http.Cors;

namespace MinoApi2.Controllers
{
   //  [EnableCors(origins: "http://10.10.114.32:58474", headers: " * ", methods: "*", SupportsCredentials = true)]
    public class DemoController
    {
        public int Index()
        {
            return 1;
        }
      
        public async Task<IEnumerable<String>> Get()
        {
            

            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            List<String> rez = new List<String>();
            try
            {
                var getListBucketsTask = await minio.ListBucketsAsync();
                //   List<Bucket> b = getListBucketsTask.Result.Buckets;

                foreach (Bucket bucket in getListBucketsTask.Buckets)
                {
                    rez.Add(bucket.Name.ToString());
                }

            }
            catch (MinioException e)
            {
                Console.WriteLine("File Upload Error: {0}", e.Message);
            }

            return rez;
        }

        public List<string> Get(int id)
        {
            return new List<string>
            {
                "Data1","Data2"
            };
        }

          [HttpGet]
    //    [Route("api/demo/set")]
        public async Task<int> Set()
        {


            var endpoint = "127.0.0.1:9000";
            var accessKey = "VDX5QGGFWH9HFGCQXY42";
            var secretKey = "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW";
            var minio = new MinioClient(endpoint, accessKey, secretKey);

            var bucketName = "mymusic";
            var location = "us-east-1";
            var objectName = "6golden-oldies.zip";
            //var filePath = "C:\\Users\\username\\Downloads\\golden_oldies.mp3";
            var filePath = "C: \\Users\\Marko\\Downloads\\apache-tomcat-8.5.23-windows-x64.zip";
            var contentType = "application/zip";

            bool found = await minio.BucketExistsAsync(bucketName);
            if (!found)
            {
                await minio.MakeBucketAsync(bucketName, location);
            }
            // Upload a file to bucket.
            await minio.PutObjectAsync(bucketName, objectName, filePath, contentType);
            //   Console.Out.WriteLine("Successfully uploaded " + objectName + ret.ToString());
             return 1;
        }

    }
}
