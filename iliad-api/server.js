const express = require('express');
const request = require('request');
const cheerio = require('cheerio');
const sendmail = require('sendmail')();
const compression = require('compression');

const app = express();

app.use(compression());

const PORT = 1335;

console.log("port: " + PORT);

// url
const ILIAD_BASE_URL = 'https://www.iliad.it/account/';
const ILIAD_OPTION_URL = {
    login: 'attivazione-della-sim',
    information: 'i-miei-dati-personali',
    credit: 'consumi-e-credito',
    voicemail: 'segreteria-telefonica',
    options: 'le-mie-opzioni',
    services: 'i-miei-servizi',
    recharge: 'rechargement',
    activation: 'attivazione-della-sim',
    document: 'le-condizioni-della-mia-offerta',
    recover: 'forget'
}
// app version
const CURRENT_APP_VERSION = '53';
// check token
const CHECK_TOKEN_TEXT = "Accedi alla tua area personale";
const ERROR_TOKEN_STATUS = 404;
// mail
const MAIL_TO_SEND = "theplayergame97@gmail.com";
const MAIL_SENDER = "Iliad@Report.it";
// document prefix 
const PRE_DOC = 'https://www.iliad.it';

var date = new Date().toISOString().split("T")[0].split("-").join("");
var hex_date = Buffer.from(date, 'utf8').toString('hex');

app.get('/', async function (req, res) {
    res.set('text/html; charset=utf-8');
    res.send("<code>Iliad Unofficial API</code>");
    //https://iliad-api-beta.glitch.me
});
// Alert
app.get('/alert', async function (req, res) {
    res.set('Content-Type', 'application/json');
    var data_store = { 'iliad': {} };
    data_store["iliad"][0] = "L’app è stata creata in modo <b>NON</b> ufficiale, iliad S.P.A non è responsabile. L’app prende le informazioni dal sito, se una sezione/testo/oggetto non c’è sul sito non ci sarà nell’app. Ti ricordo inoltre che prima di creare una valutazione sul PlayStore di contattarci su Telegram con <b>@Fast0n</b> o <b>@Mattvoid</b> oppure per email all’indirizzo <b>theplayergame97@gmail.com</b>.<br/>Grazie per l’attenzione."
    res.send(data_store);
});
// Login
app.get('/login', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var userid = req.query.userid;
    var password = req.query.password;
    var token = req.query.token;

    var data_store = { 'iliad': {} };
  
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    if (userid && password && token) {
        var formData = {
            'login-ident': userid,
            'login-pwd': Buffer.from(password + '', 'base64').toString('utf8')
        }
        console.log("userid : " + userid);
        console.log("password : " + Buffer.from(password + '', 'base64').toString('utf8'));
        console.log("------------------------");
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['login'],
            method: 'POST',
            headers: headers,
            formData: formData,
            followAllRedirects: true,
        };

        request(options, function (error, response, body) {
            if (!error) {
                try {
                    const $ = cheerio.load(body);
                  
                    var nav = $('div.current-user').first().text().replace(/^\s+|\s+$/gm, '').split('\n');
                    var check = $('div.steps-icon__text').find('p.green').text();
                    var menu_num = 0;
                    
                    $('ul.menu').first()
                    .find('li')
                    .each(function (i, element) {
                        menu_num++;
                    });
                  
                    data_store["iliad"] = {};

                    data_store["iliad"]["version"] = CURRENT_APP_VERSION;
                    data_store["iliad"]["user_name"] = nav[0];
                    data_store["iliad"]["user_id"] = nav[1];
                    data_store["iliad"]["user_numtell"] = nav[2];
                    
                    
                    if (check == 'SIM attivata' || menu_num == 9) {
                        data_store["iliad"]["sim"] = 'true';
                    } else {
                        data_store["iliad"]["sim"] = 'false';
                    }
                    
                    // data_store["iliad"]["sim"] = 'false'; // to delete
                    
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }
            };
        });
    } else {
        res.sendStatus(400);
    }
});
//get token
app.get('/token', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var userid = req.query.userid;
    var password = req.query.password;

    if (userid && password) {
        var formData = {
            'login-ident': userid,
            'login-pwd': password
        }

        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['login'],
            method: 'POST',
            formData: formData
        };

        request(options, function (error, response, body) {
            data_store["iliad"][0] = response['headers']['set-cookie'][0].split(';')[0].split('=')[1];
            res.send(["iliad"][0] = response['headers']['set-cookie'][0].split(';')[0].split('=')[1]);
        });
    } else {
        res.sendStatus(400);
    }
});
//logout
app.get('/logout', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var token = req.query.token;

    if (token) {
        var headers = {
            'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso  
        }
        var options = {
            url: ILIAD_BASE_URL + "?logout=user",
            method: "GET",
            headers: headers
        }
        request(options, function (error, response, body) {
            data_store["iliad"][0] = "true";
            res.send(data_store);
        });

    } else {
        res.sendStatus(400);
    }
})
//recupero password
app.get('/recover', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var email = req.query.email;
    var userid = req.query.userid;
    var token = req.query.token;
    var name = req.query.name;
    var surname = req.query.surname;
    
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (email && userid && token) {
        var formData = {
            login: userid,
            email: email
        };
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recover'],
            method: 'POST',
            formData: formData
        };

        request(options, function (error, response, body) {
            try {
                if (!error && response.statusCode == 200) {

                    data_store["iliad"][0] = 'true';
                    res.send(data_store);
                    //data_store["iliad"][0] = ''; //flash-error

                } else {
                    data_store["iliad"][0] = 'true'
                    res.send(data_store);
                }
            } catch (exeption) {
                res.sendStatus(503);
            }
        })
    }
    else if (email && name && surname && token) {
        var formData = {
            nom: surname,
            prenom: name,
            email: email
        };
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recover'],
            method: 'POST',
            formData: formData
        };

        request(options, function (error, response, body) {
            try {
                if (!error && response.statusCode == 200) {
                    data_store["iliad"][0] = 'false'
                    res.send(data_store);

                } else {
                    data_store["iliad"][0] = 'true'
                    res.send(data_store);
                }
            } catch (exeption) {
                res.sendStatus(503);
            }
        })
    }
    else {
        res.sendStatus(400);
    }
});
// I miei dati personali
app.get('/information', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var info = req.query.info;
    var token = req.query.token;
    // puk
    var puk = req.query.puk;
    var password = req.query.password;
    // change password
    var new_password = req.query.new_password;
    var new_password_confirm = req.query.new_password_confirm;
    // change email
    var email = req.query.email;
    var email_confirm = req.query.email_confirm;
    // change address
    var address = req.query.address;
    var action = req.query.action;
    var province = req.query.province;
    var ville = req.query.ville;
    var code_postal = req.query.code_postal;
    var rue_nom = req.query.rue_nom;
    var fraction = req.query.fraction;
    var type_adr = req.query.type_adr;
    var rue_numero = req.query.rue_numero;
    var introuvable = req.query.introuvable;
    var complement = req.query.complement;
    var sms_validation = req.query.sms_validation;
    //change payment method
    var method = req.query.method;
    //cb
    var cbtype = req.query.cbtype;
    var cbnumero = req.query.cbnumero;
    var cbexpmois = req.query.cbexpmois;
    var cbexpannee = req.query.cbexpannee;
    var cbcrypto = req.query.cbcrypto;
    //sepa
    var sepatitulaire = req.query.sepatitulaire;
    var sepabic = req.query.sepabic;
    var sepaiban = req.query.sepaiban;
    
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token // access cookie
    };

    if (info == 'true' && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'],
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        var icon = [
                            [
                                "http://android12.altervista.org/res/ic_edit.png",
                                "http://android12.altervista.org/res/ic_adress.png"
                            ],
                            [
                                "http://android12.altervista.org/res/ic_edit.png",
                                "http://android12.altervista.org/res/ic_credit_card.png"
                            ],
                            [
                                "http://android12.altervista.org/res/ic_edit.png",
                                "http://android12.altervista.org/res/ic_email.png"
                            ],
                            [
                                "http://android12.altervista.org/res/ic_edit.png",
                                "http://android12.altervista.org/res/ic_puk.png"
                            ],
                            [
                              "http://android12.altervista.org/res/ic_show.png",
                              "http://android12.altervista.org/res/ic_password.png"
                            ]
                        ]

                        $('div.infos__content')
                        .each(function (i, element){
                            data_store["iliad"][i] = {};
                            var array = $(element).find('div.infos__text').text().replace(/^\s+|\s+$/gm, '').split('\n');
                            array.push(icon[i][0]);
                            array.push(icon[i][1]);
                            var count = 0;
                            array.forEach(function(element, j) {
                                if (element == "Clicca qui") element = "xxxxxx";
                                if (!(i == 0 && j == 3)){
                                  data_store["iliad"][i][count] = element;
                                  count++;
                                }
                            });
                        });
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                } 
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    // richiesta per ottenere il puk
    else if (puk == 'true' && token) {
        var options = {
            method: 'GET',
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'],
            qs: {
                show: 'puk'
            },
            headers: {
                'x-requested-with': 'XMLHttpRequest',
                cookie: 'ACCOUNT_SESSID=' + token,
            },
            json: true
        };
        request(options, function (error, response, body) {
            try {
                
                const $ = cheerio.load(body);
              
                var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                if (check_token != CHECK_TOKEN_TEXT){
                    data_store["iliad"][0] = {}
                    if (body[0]["result"]["data"]) {
                        data_store["iliad"][0] = body[0]["result"]["data"]["code_puk"];
                        res.send(data_store);
                    } else {
                        data_store["iliad"][0] = 'Codice PUK non disponibile';
                        res.send(data_store);
                    }
                }
                else{ 
                    res.sendStatus(ERROR_TOKEN_STATUS);
                }
                
            }
            catch (exeption) {
                res.sendStatus(503);
            }
        });

    }
    // richiesta per cambiare la mail
    else if (email && email_confirm && password && token) {
        var formData = {
            email: email,
            'email-confirm': email_confirm,
            password: Buffer.from(password + '', 'base64').toString('utf8')
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + '/email',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error) {
                const $ = cheerio.load(body);
                
                var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                
                if (check_token != CHECK_TOKEN_TEXT){
                    try {
                        data_store['iliad'][0] = 'true';
                        res.send(data_store);
                    } catch (exeption) {
                        res.sendStatus(503);
                    }
                }
                else{ 
                    res.sendStatus(ERROR_TOKEN_STATUS);
                }
                    

            }
        });
    }
    // richiesta per cambiare la password
    else if (new_password && new_password_confirm && password && token) {
        // Cambio password
        var formData = {
            'password-current': Buffer.from(password + '', 'base64').toString('utf8'),
            'password-new': Buffer.from(new_password + '', 'base64').toString('utf8'),
            'password-new-confirm': Buffer.from(new_password_confirm + '', 'base64').toString('utf8'),
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + '/password',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error) {
                const $ = cheerio.load(body);
                
                var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                
                if (check_token != CHECK_TOKEN_TEXT){
                    try {
                        data_store['iliad'][0] = 'true';
                        res.send(data_store);
                    } catch (exeption) {
                        res.sendStatus(503);
                    }
                    
                }
                else{ 
                    res.sendStatus(ERROR_TOKEN_STATUS);
                }
            }
        }); //site_url + "?phonecharge=true&montant=" + montant.replace("€", "") + "&cbtype=" + typecard + "&cbnumero=" + nCard.getText().toString().replaceAll("\\s+", "") + "&cbexpmois=" + nExpiration.getText().toString().split("/")[0] + "&cbexpannee=20" + nExpiration.getText().toString().split("/")[1] + "&cbcrypto=" + ncvv.getText().toString() + "&token=" + token
    }
    // richiesta per cambiare il metodo di pagamento
    else if (password && method && token) {
        var formData = {};
        if (method == 'aucun') {
            formData = {
                'mode-paiement': method,
                password: Buffer.from(password + '', 'base64').toString('utf8')
            };
        } else if (method == 'cb') {
            formData = {
                'mode-paiement': method,
                'cb-type': cbtype,
                'cb-numero': cbnumero,
                'cb-exp-mois': cbexpmois,
                'cb-exp-annee': cbexpannee,
                'cb-crypto': cbcrypto,
                password: Buffer.from(password + '', 'base64').toString('utf8')
            };
        } else if (method == 'seba') {
            formData = {
                'mode-paiement': method,
                'sepa-titulaire': sepatitulaire,
                'sepa-bic': sepabic,
                'sepa-iban': sepaiban,
                password: Buffer.from(password + '', 'base64').toString('utf8')
            };
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + '/paiement',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error) {
                try {
                    const $ = cheerio.load(body);
                
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                    if (check_token != CHECK_TOKEN_TEXT){
                        var error = $('div.flash.flash-error').text().replace(/^\s+|\s+$/gm, '').split('\n')[0];
                        
                        if (error == null) {
                            data_store["iliad"][0] = "true";
                        } else {
                            data_store["iliad"][0] = error;
                        }
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                  
                }
                catch (e) {
                    res.sendStatus(503)
                }
            }
        });
    }
    // conferma indirizzo
    // https://iliad-api-beta.glitch.me/information/?address=true&sms_validation=6767676769&token=ezhrgxffy32eu5kh49wk
    else if (address && sms_validation && token){
        var formData = {
            'sms-validation': sms_validation,
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      const $ = cheerio.load(body);
                
                      var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                      if (check_token != CHECK_TOKEN_TEXT){
                          var error = $('div.flash.flash-error').text().replace(/^\s+|\s+$/gm, '').split('\n')[0];
                          if (!error) {
                              data_store["iliad"][0] = "true";
                          } else {
                              data_store["iliad"][0] = error;
                          }
                          res.send(data_store);
                      }
                      else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                      }
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // richiesta per cambiare l'indirizzo
    else if (address && type_adr && province && ville && code_postal && fraction && rue_nom && rue_numero && introuvable && complement && token){
        var formData = {
            type_adr: type_adr,
            province: province,
            ville: ville,
            code_postal: code_postal,
            fraction: fraction,
            rue_nom: rue_nom,
            rue_numero: rue_numero,
            introuvable: introuvable,
            complement: complement
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      const $ = cheerio.load(body);
                
                      var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                      if (check_token != CHECK_TOKEN_TEXT){
                          var check = $('input[name="sms-validation"]');
                          if (check) {
                              data_store["iliad"][0] = "true";
                          } else {
                              data_store["iliad"][0] = error;
                          }
                          res.send(data_store)
                      }
                      else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                      }
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere i numeri civici disponibili
    // https://iliad-api-beta.glitch.me/information/?address=true&rue_nom=GUIDO%20RENI%20(VIA)&ville=001272&fraction=true&token=ezhrgxffy32eu5kh49wk
    else if (address && rue_nom && ville && fraction && token) {
        var formData = {
            action: "get_adresse_from_voie",
            ville: ville,
            rue_nom: rue_nom,
            fraction: fraction
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: {
                  'x-requested-with': 'XMLHttpRequest',
                  cookie: 'ACCOUNT_SESSID=' + token,
            },
            formData: formData,
            json: true
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      const $ = cheerio.load(body);
                
                      var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                      if (check_token != CHECK_TOKEN_TEXT){
                          data_store["iliad"]["civic_number"] = body[0]["datas"]["values"];
                          res.send(data_store);
                      }
                      else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                      }
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere le vie disponibili
    // https://iliad-api-beta.glitch.me/information/?address=true&code_postal=10136&ville=001272&fraction=true&token=ezhrgxffy32eu5kh49wk
    else if (address && code_postal && ville && fraction && token) {
        var formData = {
            action: "get_voie_from_fraction",
            ville: ville,
            code_postal: code_postal,
            fraction: fraction
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: {
                  'x-requested-with': 'XMLHttpRequest',
                  cookie: 'ACCOUNT_SESSID=' + token,
            },
            formData: formData,
            json: true
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      const $ = cheerio.load(body);
                
                      var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                      if (check_token != CHECK_TOKEN_TEXT){
                          data_store["iliad"]["streets"] = body[0]["datas"]["values"];
                          res.send(data_store);
                      }
                      else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                      }
                      
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere la località
    // https://iliad-api-beta.glitch.me/information/?address=true&code_postal=10136&ville=001272&token=ezhrgxffy32eu5kh49wk
    else if (address && code_postal && ville && token) {
        var formData = {
            action: "get_fraction_from_cp",
            code_postal: code_postal,
            ville: ville
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: {
                  'x-requested-with': 'XMLHttpRequest',
                  cookie: 'ACCOUNT_SESSID=' + token,
            },
            formData: formData,
            json: true
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                    
                      const $ = cheerio.load(body);
                
                      var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');

                      if (check_token != CHECK_TOKEN_TEXT){
                          data_store["iliad"]["fraction"] = body[0]["datas"]["values"];
                          res.send(data_store);
                      }
                      else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                      }
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere i cap
    // https://iliad-api-beta.glitch.me/information/?address=true&province=TO&ville=001272&token=ezhrgxffy32eu5kh49wk
    else if (address && province && ville && token) {
        var formData = {
            action: "get_cp_from_commune",
            province: province,
            ville: ville
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: {
                  'x-requested-with': 'XMLHttpRequest',
                  cookie: 'ACCOUNT_SESSID=' + token,
            },
            formData: formData,
            json: true
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      
                      data_store["iliad"]["cap"] = body[0]["datas"]["values"];
                      res.send(data_store);
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere i cap
    // https://iliad-api-beta.glitch.me/information/?address=true&province=TO&token=ezhrgxffy32eu5kh49wk
    else if (address && province && token) {
        var formData = {
            action: "get_commune_from_province",
            province: province
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'POST',
            headers: {
                  'x-requested-with': 'XMLHttpRequest',
                  cookie: 'ACCOUNT_SESSID=' + token,
            },
            formData: formData,
            json: true
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      data_store["iliad"]["province"] = [];
                      for(var i = 0; i < Object.keys(body[0]["datas"]["values"]).length; i++){
                          data_store["iliad"]["province"][i] = body[0]["datas"]["values"][Object.keys(body[0]["datas"]["values"])[i]];
                      }
                      res.send(data_store);
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // per ottenere le città
    // https://iliad-api-beta.glitch.me/information/?address=true&token=ezhrgxffy32eu5kh49wk
    else if (address && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'] + "/indirizzo",
            method: 'GET',
            headers: headers,
        };
        request(options, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                  try{
                      const $ = cheerio.load(body);
                      var results = $('body');
                      results.each(function (i, result) {
                          data_store["iliad"]["province"] = {};
                          data_store["iliad"]["province"]["value"] = [];
                          data_store["iliad"]["province"]["name"] = [];
                          data_store["iliad"]["type"] = {};
                          data_store["iliad"]["type"]["value"] = [];
                          data_store["iliad"]["type"]["name"] = [];
                          
                          $('select[name="type_adr"]')
                          .find('option')
                          .each(function (index, element) {
                              if (index != 0){
                                  data_store["iliad"]["type"]["name"][index - 1] = $(element).text();
                                  data_store["iliad"]["type"]["value"][index - 1] = $(element).val();
                              }
                          });
                          $('select[name="province"]')
                          .find('option')
                          .each(function (index, element) {
                              if (index != 0){
                                  data_store["iliad"]["province"]["name"][index - 1] = $(element).text();
                                  data_store["iliad"]["province"]["value"][index - 1] = $(element).val();
                              }
                          });
                      });
                      res.send(data_store);
                  }
                  catch (exeption) {
                      res.sendStatus(503);
                  }
              }
        });
        
    }
    // errate request
    else {
        res.sendStatus(400);
    }
});
// Attivazione sim
app.get('/sim', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var iccid = req.query.iccid;
    var token = req.query.token;
    var activation_sim = req.query.activation_sim;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (iccid && token) {
        var formData = {
            iccid: iccid
        }

        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['activation'],
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                    
                        data_store["iliad"]["sim"] = {};

                        var sim = $('div.flash-error').text().replace(/^\s+|\s+$/gm, '').split('\n')[1];
                        
                        data_store["iliad"]["sim"][0] = sim;
                      
                        if (sim != 'L\'état actuel de votre SIM ne requiert aucune activation.' && sim != 'Cette SIM a été résiliée et ne peux plus être utilisée.') {
                            data_store["iliad"]["sim"][1] = "false";
                        } else {
                            data_store["iliad"]["sim"][1] = "true";
                        }

                        res.send(data_store);
                    }
                    else {
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
  
                }
                catch (exeption) {
                    // console.log(exeption);
                    res.sendStatus(503);
                }
            }
        });
    } 
    else if (activation_sim == 'true' && token) {
        var options = {
            url: ILIAD_BASE_URL + 'attivazione-della-sim',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        
                        data_store["iliad"]["validation"] = {};
                        data_store["iliad"]["shipping"] = {};
                        data_store["iliad"]["sim"] = {};

                        var tracking = $('a.red').attr('href');
                        var check = $('div.steps-icon__text').find('p.green').text();
                        var offert = $('h2.title').first().text().replace(/^\s+|\s+$/gm, '');

                        var title;

                        $('div.steps-icon__text')
                        .each(function (index, element) {

                            title = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n');

                            if(index == 0){
                                data_store["iliad"]["validation"][0] = title[0];
                                data_store["iliad"]["validation"][1] = title[1];
                                data_store["iliad"]["validation"][2] = title[2];
                            }
                            else if(index == 2){
                                data_store["iliad"]["shipping"][0] = title[0];
                                data_store["iliad"]["shipping"][1] = title[1];
                                data_store["iliad"]["shipping"][2] = "Non disponibile";
                                if (tracking) data_store["iliad"]["shipping"][3] = tracking; //tracking
                            }
                            else if(index == 3){
                                data_store["iliad"]["sim"][0] = title[0];
                                data_store["iliad"]["sim"][1] = title[1];
                                if (check == 'SIM attivata')
                                    data_store["iliad"]["sim"][2] = 'true';
                                else
                                    data_store["iliad"]["sim"][2] = 'false';
                                
                                // data_store["iliad"]["sim"][2] = 'false'; // to delete
                                
                                data_store["iliad"]["sim"][3] = offert;
                            }
                        });
                        
                        res.send(data_store);
                    }
                    else {
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else {
        res.sendStatus(400);
    }
});
// Credit
app.get('/credit', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var estero = req.query.estero;
    var credit = req.query.credit;
    var details = req.query.details;
  
    var history = req.query.history;

    var token = req.query.token;
    // set cookies
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (credit || estero && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'],
            method: 'GET',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    
                    var start, end;
                    
                    if (credit) { start = 0; end = 4; }
                    else { start = 4; end = 8; }

                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        
                        
                      
                        var credito_residuo = $('h2').find('b.red').text().replace(/^\s+|\s+$/gm, '');
                        var data_rinnovo = $('div.end_offerta').text().replace(/^\s+|\s+$/gm, '').match(/\d{2}\/\d{2}\/\d{4}/);
                        data_store["iliad"][0] = {};
                        data_store["iliad"][0][0] = credito_residuo + '&\n' + data_rinnovo; //titole credito
                        data_store["iliad"][0][1] = 'true'; //ricarica button
                        data_store["iliad"][0][2] = 'true'; //info consumi button
                      
                        var icon = [
                            "http://android12.altervista.org/res/ic_call.png",
                            "http://android12.altervista.org/res/ic_sms.png",
                            "http://android12.altervista.org/res/ic_gb.png",
                            "http://android12.altervista.org/res/ic_mms.png",
                            // esteri
                            "http://android12.altervista.org/res/ic_call.png",
                            "http://android12.altervista.org/res/ic_sms.png",
                            "http://android12.altervista.org/res/ic_gb.png",
                            "http://android12.altervista.org/res/ic_mms.png"
                        ];
                        
                        $('div.grid-c.w-4.w-tablet-4')
                        .each(function (index, element) {
                            if (index >= start && index < end){
                                data_store["iliad"][index - start + 1] = {};
                                data_store["iliad"][index - start + 1][0] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[0];
                                data_store["iliad"][index - start + 1][1] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[1];
                                data_store["iliad"][index - start + 1][2] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[2];
                                data_store["iliad"][index - start + 1][3] = icon[index];
                            }
                        });
                        data_store["iliad"][5] = { 0: CURRENT_APP_VERSION }; // version
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } 
    else if (details == 'true' && token) {
        var options = {
            method: 'GET',
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'] + "?historyId=" + history,
            // url: ILIAD_BASE_URL + "conso-et-factures?historyId=10",
            headers: {
                'x-requested-with': 'XMLHttpRequest',
                cookie: 'ACCOUNT_SESSID=' + token
            }
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var title;
                    var text;
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        var check = $('div.no-conso.table-montant.color-main.text-center').attr("style");
                        

                            data_store["iliad"]["title"] = {};

                            $('div.table-details')
                            .each(function (i, element) {

                                title = $(element).find('div').first().text().replace(/^\s+|\s+$/gm, '');
                                data_store["iliad"]["title"][i] = title;

                                if (i < 3){
                                    data_store["iliad"][i] = {};

                                    $(element)
                                    .find('div.grid-l.line.local')
                                    .each(function (j, row) {
                                        data_store["iliad"][i][j] = {};

                                        $(row)
                                        .find('div.grid-c')
                                        .each(function (x, text) {
                                            text = $(text).text().replace(/^\s+|\s+$/gm, '').split('\n');
                                            data_store["iliad"][i][j][x] = text[0] + " " + text[1];
                                        });

                                    });

                                }

                            });

                            data_store["iliad"][3] = {};
                            data_store["iliad"][3][0] = "";
                            data_store["iliad"][0][0];
                            console.log(data_store["iliad"][0][0][0]);
                            res.send(data_store);
                    }
                    else{ 
                          res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (history == 'true'){
        
        var options = {
            method: 'GET',
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'] + "?historyId",
            headers: headers
        };
        request(options, async function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try{
                    data_store["iliad"]["date"] = [];
                    
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    console.log(check_token);
                    if (check_token != CHECK_TOKEN_TEXT){
                        $("select.mdc-select__input")
                        .find("option")
                        .each(function (i, element) {
                            data_store["iliad"]["date"].push($(element).text());
                        });
                        
                        res.send(data_store);
                    }
                    else{ 
                      res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                }
                catch(e){
                    res.sendStatus(503);
                }
            }
        });
        
    }
    else {
        res.sendStatus(400);
    }
});
// I Miei Servizi
app.get('/services', async function (req, res) {
    res.set('Content-Type', 'application/json');
    // init data_store
    var data_store = { 'iliad': {} };
    // access token
    var token = req.query.token;
    // var to get services
    var services = req.query.services;
    // vars to change services
    var change_services = req.query.change_services;
    var update = req.query.update;
    var activate = req.query.activate;
    // vars get services info
    var info = req.query.info;
    var type = req.query.type;
    
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    
    if (services == 'true' && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['services'],
            method: 'GET',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        
                        var title = $('h1.page__title').text().replace(/^\s+|\s+$/gm, '');
                        var status_text;

                        data_store["iliad"][0] = {'0': title};
                        $('div.grid-l.as__item')  
                        .each(function (i, element) {
                            status_text = $(element).find("div.as__status--active").text().replace(/^\s+|\s+$/gm, '');
                            data_store["iliad"][i + 1] = {};
                            data_store["iliad"][i + 1][0] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[0];
                            data_store["iliad"][i + 1][1] = status_text;
                            if (status_text == "Si")
                                data_store["iliad"][i + 1][2] = "true";
                            else if (status_text == "No")
                                data_store["iliad"][i + 1][2] = "false";
                            data_store["iliad"][i + 1][3] = $(element).find('div.as_status--action').find("a").attr('href').split('=')[1].split('&')[0];
                            data_store["iliad"][i + 1][4] = $(element).find('div.grid-c.w-desktop-8.as__cell.as__item__name').find('a').attr('href').split('/')[3];
                        });
                        res.send(data_store);
                        
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }

                }
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (change_services == 'true' && activate && update && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['services'] + '?update=' + update + '&activate=' + activate,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store['iliad'][0] = 'true';
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (info == 'true' && type && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['services'] + "/" + type,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store["iliad"][0] = $('div.grid-c.w-desktop-8.as__item__name.as__cell').text().replace(/^\s+|\s+$/gm, ''); // get service name
                        data_store["iliad"][1] = $('div.service-description').text().replace(/^\s+|\s+$/gm, ''); // get service description
                        var update = $('div.grid-l.as__item.as__item--main').find('a').first().attr('href');
                      
                        if (update) data_store["iliad"][2] = update.split('=')[1]; // get option activation
                        else data_store["iliad"][2] = "";
                      
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                }
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else {
        res.sendStatus(400);
    }
});
// Report
app.get('/support', async function (req, res){
    res.set('Content-Type', 'application/json');
  
    var title = req.query.title;
    var message = req.query.message;
    var screen = req.query.screen;
    
    if (message && title && screen){
        sendmail({
          from: MAIL_SENDER, // sender address
          to: MAIL_TO_SEND, // list of receivers
          subject: title.split("%20").join(" "), // Subject line
          html: "<b>Error:</b><br><code>" + message.split("%20").join(" ") + "</code><br><br><b>Activity:</b><br>" + screen.split("%20").join(" ") // html body
        }, function (err, reply) {})
        res.send("Ok");
    }
    else
      res.sendStatus(503);
    
});
// Document
app.get('/document', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
    
    var token = req.query.token;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['document'],
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        $('div.conso__text')
                        .each(function (i, element){
                            data_store["iliad"][i] = {};
                            data_store["iliad"][i][0] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[0];
                            data_store["iliad"][i][1] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[1];
                            data_store["iliad"][i][2] = PRE_DOC + $(element).find('a').attr('href');
                        });
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                } 
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});
// Le mie opzioni
app.get('/options', async function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };
  
    var option = req.query.option;
    var token = req.query.token;
    var update = req.query.update;
    var activate = req.query.activate;
    var change_options = req.query.change_options;
    // vars get services info
    var info = req.query.info;
    var type = req.query.type;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (option == 'true' && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['options'],
            method: 'GET',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        
                        var title = $('h1.page__title').text().replace(/^\s+|\s+$/gm, ''); // get page title
                        var status_text;
                        var info; // var to store link to get info about option
                        var update; // var to store link to active or disactive option

                        data_store["iliad"][0] = {'0': title};
                        $('div.grid-l.as__item')  
                        .each(function (i, element) {
                            status_text = $(element).find("div.as__status--active").text().replace(/^\s+|\s+$/gm, '');
                            data_store["iliad"][i + 1] = {};
                            data_store["iliad"][i + 1][0] = $(element).text().replace(/^\s+|\s+$/gm, '').split('\n')[0]; // get option name
                            /* check activation status option */
                            if (status_text == "Si" || status_text == "Attivo")
                                data_store["iliad"][i + 1][2] = "true";
                            else
                                data_store["iliad"][i + 1][2] = "false";
                            
                            /* check and set option information link */
                            info = $(element).find("a").attr('href').split('/')[3];
                            if (info) data_store["iliad"][i + 1][4] = info;
                            else data_store["iliad"][i + 1][4] = "";
                            
                            /* check and set option change link */
                            update = $(element).find("div.as_status--action").find("a").attr("href");
                            console.log(update);
                            if (update) data_store["iliad"][i + 1][3] = update.split('=')[1].split('&')[0];
                            else data_store["iliad"][i + 1][3] = "";
                            
                        });
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                }
                catch (exeption) {
                    console.log(exeption);
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (info == 'true' && type && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['options'] + "/" + type,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store["iliad"][0] = $('div.grid-c.w-desktop-8.as__item__name.as__cell').first().text().replace(/^\s+|\s+$/gm, ''); // get option name
                        data_store["iliad"][1] = $('div.option-description').text().replace(/^\s+|\s+$/gm, ''); // get option description
                        var update = $('div.grid-l.as__item.as__item--main').find('a').first().attr('href');
                        
                        if (update)data_store["iliad"][2] = update.split('=')[2]; // get option activation
                        else data_store["iliad"][2] = "";
                      
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                }
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (change_options == 'true' && update && activate && token) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['options'] + '?update=' + update + '&activate=' + activate,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                  
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store['iliad'][0] = 'true';
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStauts(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    console.log(exeption);
                    res.sendStatus(503);
                }
            }
        });
    }
    else {
        res.sendStatus(400);
    }
});
// Ricarica credito
app.get('/recharge', async function (req, res) {
    
    var data_store = { 'iliad': {} };
  
    var cbtype = req.query.cbtype;
    var cbnumero = req.query.cbnumero;
    var montant = req.query.montant;
    var cbexpmois = req.query.cbexpmois;
    var cbexpannee = req.query.cbexpannee;
    var cbcrypto = req.query.cbcrypto;
    var payinfoprice = req.query.payinfoprice;
    var payinfocard = req.query.payinfocard;
    var token = req.query.token;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    //site_url + "?phonecharge=true&montant=" + montant.replace("€", "") + "&cbtype=" + typecard + "&cbnumero=" + nCard.getText().toString().replaceAll("\\s+", "") + "&cbexpmois=" + nExpiration.getText().toString().split("/")[0] + "&cbexpannee=20" + nExpiration.getText().toString().split("/")[1] + "&cbcrypto=" + ncvv.getText().toString() + "&token=" + token
    if (montant && cbtype && cbnumero && cbexpmois && cbexpannee && cbcrypto && token) {
        // Esecuzione ricarica
        var formData = {
            'cb-type': cbtype,
            'cb-numero': cbnumero,
            'cb-exp-mois': cbexpmois,
            'cb-exp-annee': cbexpannee,
            'cb-crypto': cbcrypto
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'] + '?montant=' + montant,
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            data_store["iliad"][0] = {}
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        if ($('div.flash-error') != null)
                            data_store["iliad"][0] = $('div.flash-error').text().replace(/^\s+|\s+$/gm, '').replace("Le montant de la transaction est incorrect.\n×", "Informazioni bancarie errate, transazione annullata.").split('\n')[0];
                        else
                            data_store["iliad"][0] = 'true';
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }

            }
        });
    } 
    else if (payinfocard == 'true' && token) {
        // Informazione per la ricarica
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'] + '?montant=5',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store["iliad"][0] = {}
                        data_store["iliad"][1] = {}

                        var cards = [];
                        var years = [];

                        $('img.creditCard')
                        .each(function (index, element) {
                            cards = cards.concat([$(element).attr('data-cc-value')]);
                        })

                        $('select[name="cb-exp-annee"].mdc-select__input')
                        .find('option')
                        .each(function (index, element) {
                            if ($(element).val())
                                years = years.concat([$(element).val().replace("20", "")]);
                        })

                        data_store["iliad"][0] = cards;
                        data_store["iliad"][1] = years;
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }

        });
    }
    else if (payinfoprice == 'true' && token) {
        // Informazione sulle possibilità di importo per la ricarica
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'],
            method: 'GET',
            headers: headers
        };

        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        var price = [];
                        $('option')
                        .each(function (i, element){
                            if($(element).val())
                              price = price.concat([$(element).val()]);
                        });

                        data_store["iliad"][0] = {}
                        data_store["iliad"][0] = price;
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                }
                catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else {
        res.sendStatus(400);
    }
});
// Segreteria
app.get('/voicemail', async function (req, res) {
    //res.set('Content-Type', 'application/json');
  
    var data_store = { 'iliad': {} };
  
    var token = req.query.token;
    var deleteaudio = req.query.deleteaudio;
    var idaudio = req.query.idaudio;
    var voicemailoptions = req.query.voicemailoptions;
    var voicemailreport = req.query.voicemailreport;
    var email = req.query.email;
    var action = req.query.action;
    var type = req.query.type;
    var voicemail = req.query.voicemail;
    var update = req.query.update;
    var changevoicemailoptions = req.query.changevoicemailoptions;
    var activate = req.query.activate;
    var codemessagerie = req.query.codemessagerie;
    var announce = req.query.announce;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (voicemail == 'true' && token) {
        // Richiesta messaggi in segreteria
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'],
            method: 'GET',
            headers: headers
        };

        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                  
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    if (check_token != CHECK_TOKEN_TEXT){
                        
                        /*
                        
                        data_store["iliad"][0] = {};
                        if ($('p.text-center').text().replace(/^\s+|\s+$/gm, '') == '') {
                            
                            data_store["iliad"][0][0] = $('h1').first().text().replace(/^\s+|\s+$/gm, '');
                            
                            $('div.msg')
                            .each(function (i, element) {
                                data_store["iliad"][i + 1] = {}

                                data_store["iliad"][i + 1][0] = $(element).find('div.msg__details__tel').text().replace(/^\s+|\s+$/gm, '');
                                data_store["iliad"][i + 1][1] = $(element).find('div.msg__details__date').text().replace(/^\s+|\s+$/gm, '').replace('\n', ' ').replace('(', '(<span style="color:#cc0000">').replace(')', '</span>)');
                                data_store["iliad"][i + 1][2] = $(element).find('source').attr('src').split('=')[1];
                                //data_store["iliad"][i + 1][2] = 'https://www.iliad.it' + $(element).find('source').attr('src');
                            });
                        } 
                        else {
                            data_store["iliad"][0][0] = $('p.text-center').text().replace(/^\s+|\s+$/gm, '');
                        }
                        
                        */
                         
                        /**/
                        
                        data_store["iliad"]["message"] = {};
                        data_store["iliad"]["message"][0] = {};
                        
                        if ($('p.text-center').text().replace(/^\s+|\s+$/gm, '') == '') {
                            
                            data_store["iliad"]["message"][0][0] = $('h1').first().text().replace(/^\s+|\s+$/gm, '');
                            
                            $('div.msg')
                            .each(function (i, element) {
                                data_store["iliad"][i + 1] = {}

                                data_store["iliad"]["message"][i + 1][0] = $(element).find('div.msg__details__tel').text().replace(/^\s+|\s+$/gm, '');
                                data_store["iliad"]["message"][i + 1][1] = $(element).find('div.msg__details__date').text().replace(/^\s+|\s+$/gm, '').replace('\n', ' ').replace('(', '(<span style="color:#cc0000">').replace(')', '</span>)');
                                data_store["iliad"]["message"][i + 1][2] = $(element).find('source').attr('src').split('=')[1];
                                //data_store["iliad"]["message"][i + 1][2] = 'https://www.iliad.it' + $(element).find('source').attr('src');
                            });
                        } 
                        else {
                            data_store["iliad"]["message"][0][0] = $('p.text-center').text().replace(/^\s+|\s+$/gm, '');
                        }
                        
                        
                        // new
                      
                        
                        var title = $('h2.page__title').first().text().replace(/^\s+|\s+$/gm, '');
                        var status;
                        
                        data_store["iliad"]["options"] = {};
                        data_store["iliad"]["options"][0] = {};
                        data_store["iliad"]["options"][0][0] = title;

                        $('div.grid-l.as__item')
                        .each(function (i, element){
                            data_store["iliad"]["options"][i + 1] = {};

                            status = $(element).find('div.as__status--active');

                            data_store["iliad"]["options"][i + 1][0] = $(element).find('div.inner').first().text().replace(/^\s+|\s+$/gm, '');
                            data_store["iliad"]["options"][i + 1][1] = status.text().replace(/^\s+|\s+$/gm, '');
                            
                            if (status.hasClass('as__status--on'))
                            
                                data_store["iliad"]["options"][i + 1][2] = "true";
                                
                            else
                                
                                data_store["iliad"]["options"][i + 1][2] = "false";
                                
                            data_store["iliad"]["options"][i + 1][3] = i.toString();
                        });
                      
                        // new 
                        
                        data_store["iliad"]["report"] = {};
                        data_store["iliad"]["report"][0] = {};
                        
                        data_store['iliad']["report"][0][0] = $('h2.page__title').text().replace(/^\s+|\s+$/gm, '').split('\n')[1];
                        data_store['iliad']["report"][0][1] = $('div.notifs__explain').text().replace(/^\s+|\s+$/gm, '').split('\n')[0]
                        data_store['iliad']["report"][0][2] = $('div.notifs__explain').text().replace(/^\s+|\s+$/gm, '').split('\n')[1]

                        $('div.notifs__list')
                        .find('div.grid-l.notifs__item')
                        .each(function (i, element){
                            data_store['iliad']["report"][i + 1] = {};
                            data_store['iliad']["report"][i + 1][0] = $(element).find('span.mdc-text-field__label').text().replace(/^\s+|\s+$/gm, '');
                            data_store['iliad']["report"][i + 1][1] = $(element).find('input[name="email"]').val();
                            data_store['iliad']["report"][i + 1][2] = $(element).find('span.mdc-select__label').text().replace(/^\s+|\s+$/gm, '');
                            data_store['iliad']["report"][i + 1][3] = $(element).find('option[selected="selected"]').text().replace(/^\s+|\s+$/gm, '');
                        });
                      
                        /**/
                      
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                      
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (deleteaudio == 'true' && idaudio && token) {
        // Eliminazione messaggio in segreteria
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'] + '/messaggio_vocale?id=' + idaudio + '&action=delete',
            method: 'GET',
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                cookie: 'ACCOUNT_SESSID=' + token,
                'accept-language': 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7,pt;q=0.6',
                accept: 'application/json, text/javascript, */*; q=0.01',
                scheme: 'https',
                method: 'GET',
                authority: 'www.iliad.it'
            },
            json: true
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};

                        data_store["iliad"][0] = body[0]["result"]["success"];
                        data_store["iliad"][1] = body[0]["result"]["msg"];

                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (idaudio && token) {
        // Richiesta singolo messaggio (per id) da segreteria
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'] + '/messaggio_vocale?id=' + idaudio,
            method: 'GET',
            headers: headers,
            encoding: null
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        res.send(body);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (changevoicemailoptions == 'true' && activate && update && token) {
        var options;
        if (codemessagerie) {
            options = {
                url: 'https://www.iliad.it /account/segreteria-telefonica',
                method: 'POST',
                headers: headers,
                formData: {
                    update: update,
                    activate: activate,
                    'code-messagerie': codemessagerie
                }
            };
        }
        else if (announce) {
            options = {
                url: 'https://www.iliad.it /account/segreteria-telefonica',
                method: 'POST',
                headers: headers,
                formData: {
                    update: update,
                    activate: activate,
                    announce: announce
                }
            };
        }
        else {
            options = {
                url: 'https://www.iliad.it/account/segreteria-telefonica?update=' + update + '&activate=' + activate,
                method: 'GET',
                headers: headers
            };
        }
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        data_store['iliad'][0] = 'true';
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else if (email && action && token) {
        //richiesta per aggiungere/eliminare le mail per la notifica della segreteria  
        var url = ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'] + '/notifiche?email=' + email + '&action=' + action;

        if (type) {
            url += '&type=' + type;
        }

        var options = {
            url: url,
            method: 'GET',
            headers: {
                'x-requested-with': 'XMLHttpRequest',
                cookie: 'ACCOUNT_SESSID=' + token,
            },
            json: true
        };

        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    
                    var check_token = $('h1.pre-titre.text-center').text().replace(/^\s+|\s+$/gm, '');
                    
                    if (check_token != CHECK_TOKEN_TEXT){
                        try {
                            data_store['iliad'][0] = body[0]['result']['msg'];
                        } catch (exeption) {
                            data_store['iliad'][0] = body[0]['msg'];
                        }
                        res.send(data_store);
                    }
                    else{ 
                        res.sendStatus(ERROR_TOKEN_STATUS);
                    }
                    
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    }
    else {
        res.sendStatus(400);
    }
});

const server = app.listen(process.env.PORT || PORT, function () {});