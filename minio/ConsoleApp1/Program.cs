using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Minio;
using Minio.DataModel;
using Minio.Exceptions;
using System.Reactive.Linq;
using System.Reactive;

namespace ConsoleApp1
{
    class Program
    {
        static void Main(string[] args)
        {
            //MinioClient minio = new MinioClient("play.minio.io:9000",
            //    "Q3AM3UQ867SPQQA43P2F",
            //    "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG"
            //    ).WithSSL();

            MinioClient minio = new MinioClient("127.0.0.1:9000",
                "VDX5QGGFWH9HFGCQXY42",
                "FSDxipLeNUX7E8Y6ttsXVhaTyGe6mnlCHsP1xWKW"
                );

            // Create an async task for listing buckets.
            var getListBucketsTask = minio.ListBucketsAsync();

            // Iterate over the list of buckets.
            foreach (Bucket bucket in getListBucketsTask.Result.Buckets)
            {
                try
                {

                    //   minio.PutObjectAsync(bucket.Name.ToString(), "Pic-1.jpg", "C:/Users/Marko/Downloads/Pic-1.jpg", contentType: "application/octet-stream");
                    // Console.Out.WriteLine("Pic-1.jpg is uploaded successfully " + "to bucket " + bucket.Name.ToString());
                    Console.Out.WriteLine(bucket.Name + " " + bucket.CreationDate.ToString());
                }
                catch (MinioException e)
                {
                    Console.Out.WriteLine("Error occurred: " + e);
                }

            }
        }


     
        // File uploader task.
        private async static Task<int> Run(MinioClient minio)
        {
            var bucketName = "mymusic";
            var location = "us-east-1";
            var objectName = "3golden-oldies.zip";
            //var filePath = "C:\\Users\\username\\Downloads\\golden_oldies.mp3";
            var filePath = "C: \\Users\\Marko\\Downloads\\apache-tomcat-8.5.23-windows-x64.zip";
            var contentType = "application/zip";

            int ret = 0;

            try
            {
                // Make a bucket on the server, if not already present.
                minio.SetTraceOff();

                bool found = await minio.BucketExistsAsync(bucketName);
                if (!found)
                {
                    await minio.MakeBucketAsync(bucketName, location);
                }
                // Upload a file to bucket.
                await minio.PutObjectAsync(bucketName, objectName, filePath, contentType);
                Console.Out.WriteLine("Successfully uploaded " + objectName + ret.ToString());

                ret = 1;
            }
            catch (MinioException e)
            {
                Console.WriteLine("File Upload Error: {0}", e.Message);
            }

            return ret;
        }

    }
}
