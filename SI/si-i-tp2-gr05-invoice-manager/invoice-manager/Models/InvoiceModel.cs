using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using Newtonsoft.Json;
using System.Xml;
using System.Text;

namespace invoice_manager.Models
{
    public class InvoiceModel
    {
        public String DATA_DIRECTORY = "Models/data/";
        public List<String> DATA_FILES = new List<string> { "bill1.json", "bill2.json", "bill3.json" };
        public string? RequestId { get; set; }

        public String GetXml(String id)
        {
            foreach (String file in DATA_FILES)
            {
                Bill bill = DesirializeInvoice(DATA_DIRECTORY + file);
                Console.WriteLine(id + " " + bill.Id);
                if (id == bill.Id)
                {
                    StringBuilder sb = new StringBuilder();
                    var content = File.ReadAllText(DATA_DIRECTORY + file);
                    
                    var resultJson = JsonConvert.DeserializeXmlNode(content);
                    XmlWriterSettings settings = new XmlWriterSettings
                    {
                        Indent = true,
                        IndentChars = "  ",
                        NewLineChars = "\r\n",
                        NewLineHandling = NewLineHandling.Replace
                    };
                    using (XmlWriter writer = XmlWriter.Create(sb, settings))
                    {
                        resultJson.Save(writer);
                    }
                    return sb.ToString();
                }
            }

            return "";
        }

        public String GetJson(String id)
        {
            foreach (String file in DATA_FILES)
            {
                Bill bill = DesirializeInvoice(DATA_DIRECTORY + file);
                Console.WriteLine(id + " " + bill.Id);
                if (id == bill.Id)
                {
                    return File.ReadAllText(DATA_DIRECTORY + file);
                }
            }

            return "" ;
            
        }


        public bool ShowRequestId => !string.IsNullOrEmpty(RequestId);

        public Bill GetInvoice(String id)
        {
            foreach (String file in DATA_FILES)
            {
                Bill bill = DesirializeInvoice(DATA_DIRECTORY + file);
                Console.WriteLine("Check if matching: "+ id + " <-> " + bill.Id);
                if (id == bill.Id)
                {
                    return bill;
                }
            }
            return null;
        }

        public List<LightBill> GetAllInvoices(string filter, String order)
        {
            bool a = false;
            if (order == "True") a = true;
            List<LightBill> result = new List<LightBill>();
            Console.WriteLine("Searching invoices...");
            foreach (String file in DATA_FILES)
            {
                Bill bill = DesirializeInvoice(DATA_DIRECTORY + file);
                LightBill lightBill = new LightBill();
                lightBill.Id = bill.Id;
                lightBill.Date = bill.Date;
                lightBill.Amount = bill.Amount;
                lightBill.Devise = bill.Devise;
                lightBill.Payed = bill.Payed;
                lightBill.Articles = bill.Articles;
                
                result.Add(lightBill);

            }

            Console.WriteLine("Sorting invoice by " + filter);
            result.Sort((bill1, bill2) => {
                var res = 0;
                switch (filter)
                {
                    case "Id":
                        res = bill1.Id.CompareTo(bill2.Id);
                        break;
                    case "Date":
                        res = bill1.Date.CompareTo(bill2.Date);
                        break;
                    case "Price":
                        res = bill1.Amount.CompareTo(bill2.Amount);
                        break;
                    case "Status":
                        res = bill1.Payed.CompareTo(bill2.Payed);
                        break;
                    case "Article":
                        var article1 = bill1.Articles[0].Brand +" "+ bill1.Articles[0].Type +" "+ bill1.Articles[0].Name;
                        var article2 = bill2.Articles[0].Brand +" "+ bill2.Articles[0].Type +" "+ bill2.Articles[0].Name;
                        res = article1.CompareTo(article2);
                        break;
                }
                if (!a)
                {
                    res *= -1;
                }
                return res;
            });

            return result;
        }

        private Bill DesirializeInvoice(String files)
        {
            var options = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
                WriteIndented = true
            };

            var jsonString = File.ReadAllText(files);
            //Console.WriteLine("Json string: " + jsonString);
            var jsonModel = System.Text.Json.JsonSerializer.Deserialize<Invoice>(jsonString, options);
            //Console.WriteLine("Json model: " + jsonModel);
            return jsonModel.Bill;
        }

    }

    public class Invoice
    {
        public Bill Bill { get; set; }
    }

    public class LightBill
    {
        public String Id { get; set; }
        public String Date { get; set; }
        public double Amount { get; set; }
        public String Devise { get; set; }
        public bool Payed { get; set; }
        public List<Article> Articles { get; set; }
    }

    public class Bill
    {
        public String Id { get; set; }
        public String Date { get; set; }
        public double Amount { get; set; }
        public String Devise { get; set; }
        public bool Payed { get; set; }
        public Client Client { get; set; }
        public Sender Sender { get; set; }
        public List<Article> Articles { get; set; }
        public Delivery Delivery { get; set; }
    }

    public class Address
    {
        public String Street { get; set; }
        public String City { get; set; }
        public int Npa { get; set; }
        public String Country { get; set; }
    }

    public class Client
    {
        public String Number { get; set; }
        public String Name { get; set; }
        public String Surname { get; set; }
        public Address Address { get; set; }
    }

    public class Sender
    {
        public String Name { get; set; }
        public Address Address { get; set; }
    }

    public class Article
    {
        public String Name { get; set; }
        public String Brand { get; set; }
        public String Type { get; set; }
        public String Quantity { get; set; }
        public double Price { get; set; }
        public double Tva { get; set; }
    }

    public class Delivery
    {
        public double Price { get; set; }
        public String Type { get; set; }
    }


}