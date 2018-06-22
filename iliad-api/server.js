const express = require('express');
const app = express();
const request = require('request');
const cheerio = require('cheerio');

app.get('/', function (req, res) {

    var userid = req.query.userid;
    var psw = req.query.password;
    const password = Buffer.from(psw + '', 'base64').toString('utf8');
    var token = req.query.token;
    var iccid = req.query.iccid;
    var email = req.query.email;
    var email_confirm = req.query.email_confirm;
    var new_password = req.query.new_password;
    var new_password_confirm = req.query.new_password_confirm;
    var info = req.query.info;
    var doc = req.query.doc;
    var credit = req.query.credit;
    var creditestero = req.query.creditestero;
    var option = req.query.option;
    var services = req.query.services;
    var update = req.query.update;
    var change_options = req.query.change_options;
    var change_services = req.query.change_services;
    var activate = req.query.activate;
    var activation_sim = req.query.activation_sim;
    var consumptiondetails = req.query.consumptiondetails;
    var consumptionroamingdetails = req.query.consumptionroamingdetails;
    var alert = req.query.alert;
    var puk = req.query.puk;
    var phonecharge = req.query.phonecharge;
    var cbtype = req.query.cbtype;
    var cbnumero = req.query.cbnumero;
    var montant = req.query.montant;
    var cbexpmois = req.query.cbexpmois;
    var cbexpannee = req.query.cbexpannee;
    var cbcrypto = req.query.cbcrypto;
    var payinfoprice = req.query.payinfoprice;
    var payinfocard = req.query.payinfocard;
    var voicemail = req.query.voicemail;
    var getNumTell = req.query.getNumTell;
    var idaudio = req.query.idaudio;
    var deleteaudio = req.query.deleteaudio;
    var voicemailoptions = req.query.voicemailoptions;
    var type = req.query.type;
    var action = req.query.action;
    var voicemailreport = req.query.voicemailreport;
    var changevoicemailoptions = req.query.changevoicemailoptions;
    var codemessagerie = req.query.codemessagerie;
    var announce = req.query.announce;

    var data_store = {};
    data_store["iliad"] = {};

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token
    };

    if (userid != undefined && password != undefined && token != undefined) {

        console.log(userid + "\n" + password + "\n");

        var formData = {
            'login-ident': userid,
            'login-pwd': password
        }

        var options = {
            url: 'https://www.iliad.it/account/attivazione-della-sim',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                const $ = cheerio.load(body);
                var results = $('body');
                try {
                    results.each(function (i, result) {
                        var nav = $(result).find('div.current-user').first().text().split('\n');
                        var check = $(result).find('div.step__text').find('p.green').text();
                        data_store["iliad"] = {};
                        data_store["iliad"]["version"] = {};
                        data_store["iliad"]["user_name"] = {};
                        data_store["iliad"]["user_id"] = {};
                        data_store["iliad"]["user_numtell"] = {};
                        data_store["iliad"]["sim"] = {};

                        data_store["iliad"]["version"] = "1111";
                        data_store["iliad"]["user_name"] = nav[1].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["user_id"] = nav[2].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["user_numtell"] = nav[3].replace(/^\s+|\s+$/gm, '');

                        if (check == 'SIM attivata') {
                            data_store["iliad"]["sim"] = 'true';
                        } else {
                            data_store["iliad"]["sim"] = 'false';
                        }
                        res.send(data_store);
                        console.log(data_store);


                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            };
        });
    } else if (activation_sim == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/attivazione-della-sim',
            method: 'POST',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');

                    var array = [];
                    var array1 = [];

                    results.each(function (i, result) {

                        data_store["iliad"]["validation"] = {};
                        data_store["iliad"]["shipping"] = {};
                        data_store["iliad"]["sim"] = {};

                        var array = [];
                        var array2 = [];
                        var array3 = [];
                        $(result)
                            .find('h2.title')
                            .each(function (index, element) {
                                array = array.concat([$(element).text()]);
                            });
                        $(result)
                            .find('div.grid-l')
                            .find('div.step__text')
                            .each(function (index, element) {
                                array2 = array2.concat([$(element).find('h4.step__text__title').text()]);
                            });
                        $(result)
                            .find('div.grid-l')
                            .find('div.step__text')
                            .each(function (index, element) {
                                array3 = array3.concat([$(element).find('a').text()]);
                            });

                        var title = $(result).find('div.form-activation').text().split('   ').join('').split('\n')
                        var orderdate = $(result).find('div.step__text').first().text().split('\n')
                        var tracking = $(result).find('a.red').attr('href')
                        var activation = $(result).find('p.explain').text().replace(/^\s+|\s+$/gm, '').split('  ').join(' ').split('\n')
                        var check = $(result).find('div.step__text').find('p.green').text();
                        var order_shipped = $(result).find('div.step__text').find('p').html()

                        activation = activation[0];
                        title = title[1];

                        var offer = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                        var order_date = orderdate[2].replace(/^\s+|\s+$/gm, '')
                        var date = orderdate[3].replace(/^\s+|\s+$/gm, '')
                        var tracking_text = array3[2]
                        var validation = array2[0]
                        var preparazione = array2[1]
                        var spedizione = array2[2]


                        data_store["iliad"]["shipping"][0] = spedizione;
                        if (order_shipped != null)
                            data_store["iliad"]["shipping"][1] = order_shipped;
                        else
                            data_store["iliad"]["shipping"][1] = 'Non disponibile';
                        data_store["iliad"]["shipping"][2] = tracking_text;
                        if (tracking != undefined)
                            data_store["iliad"]["shipping"][3] = tracking;
                        else
                            data_store["iliad"]["shipping"][3] = 'https://www.brt.it/fe-web/definition/errorpage.jsp';
                        if (title != undefined)
                            data_store["iliad"]["sim"][0] = title;
                        else
                            data_store["iliad"]["sim"][0] = 'Non disponibile';
                        if (activation != undefined)
                            data_store["iliad"]["sim"][1] = activation;
                        else
                            data_store["iliad"]["sim"][1] = 'Non disponibile';

                        if (check == 'SIM attivata') {
                            data_store["iliad"]["sim"][2] = 'true';
                        } else {
                            data_store["iliad"]["sim"][2] = 'false';
                        }

                        data_store["iliad"]["sim"][3] = offer;
                        data_store["iliad"]["validation"][0] = validation;
                        data_store["iliad"]["validation"][1] = order_date;
                        data_store["iliad"]["validation"][2] = date;

                        res.send(data_store)

                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (credit == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/consumi-e-credito',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    var array2 = [];
                    results.each(function (i, result) {

                        $(result)
                            .find('div.conso__content')
                            .each(function (index, element) {
                                array = array.concat([$(element).find('div.conso__text').text()]);
                            });
                        $(result)
                            .find('div.conso__icon')
                            .each(function (index, element) {
                                array2 = array2.concat([$(element).find('div.wrapper-align').text()]);
                            });

                        ;
                        var title = $(result).find('h2').find('b.red').text().replace(/^\s+|\s+$/gm, '');
                        var title2;
                        $(result).find('div.table-montant').find('div.label').each(function (index, element) {
                            if (index == 1)
                                title2 = $(element).text().replace(/^\s+|\s+$/gm, '')
                        })
                        try {
                            var chiamate_title = array2[0].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            var sms_title = array2[1].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            var data_title = array2[2].split('\n')[5].replace(/^\s+|\s+$/gm, '');
                            var mms_tittle = array2[3].split('\n')[2].replace(/^\s+|\s+$/gm, '');

                            var chiamate = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                            var consumi_voce = array[0].split('\n')[2].replace(/^\s+|\s+$/gm, '')
                            var sms = array[1].split('\n')[0].replace(/^\s+|\s+$/gm, '')
                            var sms_extra = array[1].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                            var data = array[2].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                            var data_consumi = array[2].split('\n')[2].replace(/^\s+|\s+$/gm, '')
                            var mms = array[3].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                            var mms_consumi = array[3].split('\n')[2].replace(/^\s+|\s+$/gm, '')
                        } catch (exeption) {
                            res.sendStatus(503);
                            res.end();
                        }


                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][2] = {};
                        data_store["iliad"][3] = {};
                        data_store["iliad"][4] = {};

                        data_store["iliad"][0][0] = title + '&' + title2;
                        data_store["iliad"][0][1] = 'true';
                        //info consumi button
                        data_store["iliad"][0][2] = 'true';

                        data_store["iliad"][1][0] = chiamate;
                        data_store["iliad"][1][1] = consumi_voce;
                        data_store["iliad"][1][2] = chiamate_title;
                        data_store["iliad"][1][3] = "http://android12.altervista.org/res/ic_call.png";

                        data_store["iliad"][2][0] = sms;
                        data_store["iliad"][2][1] = sms_extra;
                        data_store["iliad"][2][2] = sms_title;
                        data_store["iliad"][2][3] = "http://android12.altervista.org/res/ic_sms.png";

                        data_store["iliad"][3][0] = data;
                        data_store["iliad"][3][1] = data_consumi;
                        data_store["iliad"][3][2] = data_title;
                        data_store["iliad"][3][3] = "http://android12.altervista.org/res/ic_gb.png";

                        data_store["iliad"][4][0] = mms;
                        data_store["iliad"][4][1] = mms_consumi;
                        data_store["iliad"][4][2] = mms_tittle;
                        data_store["iliad"][4][3] = "http://android12.altervista.org/res/ic_mms.png";

                        res.send(data_store);
                        return;
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (creditestero == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/consumi-e-credito',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    var array2 = [];
                    results.each(function (i, result) {

                        $(result)
                            .find('div.conso__content')
                            .each(function (index, element) {
                                array = array.concat([$(element).find('div.conso__text').text()]);
                            });
                        $(result)
                            .find('div.conso__icon')
                            .each(function (index, element) {
                                array2 = array2.concat([$(element).find('div.wrapper-align').text()]);
                            });

                        var title = $(result).find('h2').text().split('\n')[1].replace(/^\s+|\s+$/gm, '');

                        var chiamate_title = array2[0].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        var sms_title = array2[1].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        var data_title = array2[2].split('\n')[5].replace(/^\s+|\s+$/gm, '');
                        var mms_tittle = array2[3].split('\n')[2].replace(/^\s+|\s+$/gm, '');

                        var chiamate = array[4].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                        var consumi_voce = array[4].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        var sms = array[5].split('\n')[0].replace(/^\s+|\s+$/gm, '');
                        var sms_extra = array[5].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        var data = array[6].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                        var data_consumi = array[6].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        var mms = array[7].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                        var mms_consumi = array[7].split('\n')[2].replace(/^\s+|\s+$/gm, '');

                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][2] = {};
                        data_store["iliad"][3] = {};
                        data_store["iliad"][4] = {};

                        data_store["iliad"][0][0] = title;
                        //ricarica button
                        data_store["iliad"][0][1] = 'true';
                        //info consumi button
                        data_store["iliad"][0][2] = 'true';

                        data_store["iliad"][1][0] = chiamate;
                        data_store["iliad"][1][1] = consumi_voce;
                        data_store["iliad"][1][2] = chiamate_title;
                        data_store["iliad"][1][3] = "http://android12.altervista.org/res/ic_call.png";

                        data_store["iliad"][2][0] = sms;
                        data_store["iliad"][2][1] = sms_extra;
                        data_store["iliad"][2][2] = sms_title;
                        data_store["iliad"][2][3] = "http://android12.altervista.org/res/ic_sms.png";

                        data_store["iliad"][3][0] = data;
                        data_store["iliad"][3][1] = data_consumi;
                        data_store["iliad"][3][2] = data_title;
                        data_store["iliad"][3][3] = "http://android12.altervista.org/res/ic_gb.png";

                        data_store["iliad"][4][0] = mms;
                        data_store["iliad"][4][1] = mms_consumi;
                        data_store["iliad"][4][2] = mms_tittle;
                        data_store["iliad"][4][3] = "http://android12.altervista.org/res/ic_mms.png";


                        res.send(data_store)
                        return;
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (email != undefined && email_confirm != undefined && password != undefined && token != undefined) {
        var formData = {
            email: email,
            'email-confirm': email_confirm,
            password: password
        }
        var options = {
            url: 'https://www.iliad.it/account/i-miei-dati-personali/email',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error) {
                try {
                    data_store['iliad'][0] = 'true';
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }

            }
        });
    } else if (new_password != undefined && new_password_confirm != undefined && password != undefined && token != undefined) {

        var formData = {
            'password-current': password,
            'password-new': Buffer.from(new_password + '', 'base64').toString('utf8'),
            'password-new-confirm': Buffer.from(new_password_confirm + '', 'base64').toString('utf8'),
        }
        var options = {
            url: 'https://www.iliad.it/account/i-miei-dati-personali/password',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error) {
                try {
                    data_store['iliad'][0] = 'true';
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }

            }
        });
    } else if (iccid != undefined && token != undefined) {
        var formData = {
            iccid: iccid
        }

        var options = {
            url: 'https://www.iliad.it/account/attivazione-della-sim',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {

                        data_store["iliad"]["sim"] = {};

                        var sim = $(result)
                            .find('div.flash-error').text().replace(/^\s+|\s+$/gm, '').split('\n')
                        sim = sim[1];
                        if (sim != 'L\'état actuel de votre SIM ne requiert aucune activation.' && sim != 'Cette SIM a été résiliée et ne peux plus être utilisée.') {
                            data_store["iliad"]["sim"][0] = sim;
                            data_store["iliad"]["sim"][1] = "false";
                        } else {
                            data_store["iliad"]["sim"][0] = sim;
                            data_store["iliad"]["sim"][1] = "true";
                        }

                        res.send(data_store);



                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (info == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/i-miei-dati-personali',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    try {
                        results.each(function (i, result) {
                            $(result)
                                .find('div.infos__content')
                                .each(function (index, element) {
                                    array = array.concat([$(element).find('div.infos__text').text()]);
                                });
                            var puk = $(result).find('span.bulle-info').attr('data-help-content');

                            var address_title = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                            var address = array[0].split('\n')[3].replace(/^\s+|\s+$/gm, '');
                            var cap = array[0].split('\n')[5].replace(/^\s+|\s+$/gm, '');
                            var pay_title = array[1].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                            var pay_method = array[1].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            var mail_title = array[2].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                            var mail = array[2].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            var password_title = array[3].split('\n')[1].replace(/^\s+|\s+$/gm, '');
                            var password = array[3].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            var puk_title = array[4].split('\n')[3].split('     ').join('');
                            var puk_text = array[4].split('\n')[7].replace(/^\s+|\s+$/gm, '');

                            data_store["iliad"][0] = {};
                            data_store["iliad"][1] = {};
                            data_store["iliad"][2] = {};
                            data_store["iliad"][3] = {};
                            data_store["iliad"][4] = {};

                            data_store["iliad"][0][0] = address_title;
                            data_store["iliad"][0][1] = address;
                            data_store["iliad"][0][2] = cap;
                            data_store["iliad"][0][3] = "";
                            data_store["iliad"][0][4] = "http://android12.altervista.org/res/ic_adress.png";

                            data_store["iliad"][1][0] = pay_title;
                            data_store["iliad"][1][1] = pay_method;
                            data_store["iliad"][1][2] = "";
                            data_store["iliad"][1][3] = "http://android12.altervista.org/res/ic_credit_card.png";


                            data_store["iliad"][2][0] = mail_title;
                            data_store["iliad"][2][1] = mail;
                            data_store["iliad"][2][2] = "http://android12.altervista.org/res/ic_edit.png";
                            data_store["iliad"][2][3] = "http://android12.altervista.org/res/ic_email.png";


                            data_store["iliad"][3][0] = password_title;
                            data_store["iliad"][3][1] = password;
                            data_store["iliad"][3][2] = "http://android12.altervista.org/res/ic_edit.png";
                            data_store["iliad"][3][3] = "http://android12.altervista.org/res/ic_puk.png";

                            data_store["iliad"][4][0] = puk_title;
                            data_store["iliad"][4][1] = 'xxxxxx';
                            data_store["iliad"][4][3] = "http://android12.altervista.org/res/ic_password.png";
                            data_store["iliad"][4][2] = "http://android12.altervista.org/res/ic_show.png"
                            res.send(data_store);




                        });
                    } catch (Exeption) {
                        res.sendStatus(503);
                        res.end();
                    }
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (puk == 'true' && token != undefined) {
        var options = {
            method: 'GET',
            url: 'https://www.iliad.it/account/mes-informations',
            qs: {
                show: 'puk'
            },
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                referer: 'https://www.iliad.it/account/mes-informations',
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
            try {
                data_store["iliad"][0] = {}
                if (body[0]["result"]["data"] != undefined) {
                    data_store["iliad"][0] = body[0]["result"]["data"]["code_puk"];
                    res.send(data_store);
                } else {
                    data_store["iliad"][0] = 'Codice PUK non disponibile';
                    res.send(data_store);
                }
            } catch (exeption) {
                res.sendStatus(503);
                res.end();
            }
        });

    } else if (option == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/le-mie-opzioni',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var status = [];
                    var text = [];
                    var array3 = [];
                    results.each(function (i, result) {

                        var title_option = $(result).find('h1').text().split('\n')[1].replace(/^\s+|\s+$/gm, '');

                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                text = text.concat([$(element).find('span.as__status__text').text()]);
                            });
                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                status = status.concat([$(element).find('i').attr('class')]);
                            });
                        $(result)
                            .find('div.bold')
                            .each(function (index, element) {
                                array3 = array3.concat([$(element).find('a').text()]);
                            });

                        var query = [
                            "",
                            "blocage_premium"
                        ];

                        var option = {};

                        for (var x = 0; x < 3; x++) {
                            option[x] = [];
                        }

                        for (var x = 0; x < Object.keys(option).length - 1; x++) {
                            option[x][0] = array3[x + 4].split('\n')[2].replace(/^\s+|\s+$/gm, '');
                            option[x][1] = text[x];
                            if (status[x] == 'icon i-check') {
                                option[x][2] = 'true';
                            } else {
                                option[x][2] = 'false';
                            }
                            option[x][3] = query[x];
                        }

                        for (var x = 0; x < 3; x++) {
                            data_store["iliad"][x] = {};
                        }

                        data_store["iliad"][0][0] = title_option;

                        for (var x = 0; x < Object.keys(option).length - 1; x++) {
                            for (var y = 0; y < option[x].length; y++) {
                                data_store["iliad"][x + 1][y] = option[x][y];
                            }
                        }
                        res.send(data_store);

                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (services == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/i-miei-servizi',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var status = [];
                    var text = [];
                    var array3 = [];
                    results.each(function (i, result) {

                        var title_service = $(result).find('h1').text().split('\n')[1].replace(/^\s+|\s+$/gm, '');

                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                text = text.concat([$(element).find('span.as__status__text').text()]);
                            });
                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                status = status.concat([$(element).find('i').attr('class')]);
                            });
                        $(result)
                            .find('div.bold')
                            .each(function (index, element) {
                                if ($(element).find('a').text().replace(/^\s+|\s+$/gm, '') != '')
                                    array3 = array3.concat([$(element).find('a').text().replace(/^\s+|\s+$/gm, '')]);
                            });

                        var query = [
                            "block_anonymous",
                            "voicemail_roaming",
                            "block_redirect",
                            "absent_subscriber",
                            "speed_dial",
                            "filter_rules"
                        ];

                        var service = {};

                        for (var x = 0; x < 7; x++) {
                            service[x] = [];
                        }

                        for (var x = 0; x < Object.keys(service).length - 1; x++) {
                            service[x][0] = array3[x];
                            service[x][1] = text[x];
                            if (status[x] == 'icon i-check') {
                                service[x][2] = 'true';
                            } else {
                                service[x][2] = 'false';
                            }
                            service[x][3] = query[x];
                        }

                        for (var x = 0; x < 7; x++) {
                            data_store["iliad"][x] = {};
                        }

                        data_store["iliad"][0][0] = title_service;

                        for (var x = 0; x < Object.keys(service).length - 1; x++) {
                            for (var y = 0; y < service[x].length; y++) {
                                data_store["iliad"][x + 1][y] = service[x][y];
                            }
                        }
                        res.send(data_store);

                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (doc == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/le-condizioni-della-mia-offerta',
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    var array2 = [];
                    results.each(function (i, result) {
                        $(result)
                            .find('div.conso__content')
                            .each(function (index, element) {
                                array = array.concat([$(element).find('div.conso__text').text()]);
                            });
                        $(result)
                            .find('div.conso__content')
                            .each(function (index, element) {
                                array2 = array2.concat([$(element).find('div.conso__text').find('a').attr('href')]);
                            });
                        var condition_title = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                        var condition_text = array[0].split('\n')[2].replace(/^\s+|\s+$/gm, '')
                        var price_title = array[1].split('\n')[1].replace(/^\s+|\s+$/gm, '')
                        var price_text = array[1].split('\n')[2].replace(/^\s+|\s+$/gm, '')
                        var condition_doc = 'https://www.iliad.it' + array2[0];
                        var price_doc = 'https://www.iliad.it' + array2[1];

                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][0][0] = condition_title;
                        data_store["iliad"][0][1] = condition_text;
                        data_store["iliad"][0][2] = condition_doc;
                        data_store["iliad"][1][0] = price_title;
                        data_store["iliad"][1][1] = price_text;
                        data_store["iliad"][1][2] = price_doc;
                        res.send(data_store);
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (change_options == 'true' && update != undefined && activate != undefined && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/le-mie-opzioni?update=' + update + '&activate=' + activate,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    data_store['iliad'][0] = 'true';
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (change_services == 'true' && activate != undefined && update != undefined && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/i-miei-servizi?update=' + update + '&activate=' + activate,
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    data_store['iliad'][0] = 'true';

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (consumptiondetails == 'true' && token != undefined) {
        var options = {
            umethod: 'GET',
            url: 'https://www.iliad.it/account/consumi-e-credito',
            qs: {
                details: ''
            },
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                referer: 'https://www.iliad.it/account/consumi-e-credito',
                cookie: 'ACCOUNT_SESSID=' + token,
                'accept-language': 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7,pt;q=0.6',
                accept: 'application/json, text/javascript, */*; q=0.01',
                scheme: 'https',
                method: 'GET',
                authority: 'www.iliad.it'
            },
            json: true
        };
        //table-details
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);

                    var voix = $('div.table-details').find('div.voix');
                    var renvoi_d_appel = $('div.table-details').find('div.renvoi-d-appel');
                    var sms = $('div.table-details').find('div.sms');
                    var data = $('div.table-details').find('div.data');


                    var table = [];

                    data_store["iliad"]["title"] = {};

                    if (voix != '') {
                        voix.each(function (i, result) {
                            data_store["iliad"]["title"][0] = voix.text().replace(/^\s+|\s+$/gm, '');
                        });
                    } else {
                        data_store["iliad"]["title"][0] = '';
                    }
                    if (renvoi_d_appel != '') {
                        renvoi_d_appel.each(function (i, result) {
                            data_store["iliad"]["title"][1] = renvoi_d_appel.text().replace(/^\s+|\s+$/gm, '');
                        });
                    } else {
                        data_store["iliad"]["title"][1] = '';
                    }
                    if (sms != '') {
                        sms.each(function (i, result) {
                            data_store["iliad"]["title"][2] = sms.text().replace(/^\s+|\s+$/gm, '');
                        });
                    } else {
                        data_store["iliad"]["title"][2] = '';
                    }
                    if (data != '') {
                        data.each(function (i, result) {
                            data_store["iliad"]["title"][3] = data.first().find('i').text().replace(/^\s+|\s+$/gm, '');
                        });
                    } else {
                        data_store["iliad"]["title"][3] = '';
                    }

                    $('div.table-details')
                        .each(function (index, element) {
                            table = table.concat([$(element).find('div.body').text()]);
                        });

                    if (table[0] != undefined) {
                        var voix_data = table[0].replace(/^\s+|\s+$/gm, '').split('\n');
                    } else {
                        var voix_data = undefined;
                    }
                    if (table[1] != undefined) {
                        var renvoi_d_appel_data = table[1].replace(/^\s+|\s+$/gm, '').split('\n');
                    } else {
                        var renvoi_d_appel_data = undefined;
                    }
                    if (table[2] != undefined) {
                        var sms_data = table[2].replace(/^\s+|\s+$/gm, '').split('\n');
                    } else {
                        var sms_data = undefined;
                    }
                    if (table[3] != undefined) {
                        var data_data = table[3].replace(/^\s+|\s+$/gm, '').split('\n');
                    } else {
                        var data_data = undefined;
                    }



                    if ($('div.no-conso').attr('style') == 'display:none;') {



                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][2] = {};
                        data_store["iliad"][3] = {};

                        if (voix_data != undefined) {
                            var add = 0
                            for (var x = 0; x < voix_data.length / 8; x++) {
                                data_store["iliad"][0][x] = {}
                                for (var y = 0; y < 8; y++) {
                                    if (y == 4) {
                                        data_store["iliad"][0][x][y] = voix_data[y + add] + ': ' + voix_data[y + add + 1]
                                    } else if (y == 5) {} else if (y == 6) {
                                        data_store["iliad"][0][x][5] = voix_data[y + add]
                                    } else {
                                        data_store["iliad"][0][x][y] = voix_data[y + add]
                                    }
                                }
                                add = add + 8;
                            }
                        } else {
                            data_store["iliad"][0] = {};
                            data_store["iliad"][0][0] = 'false';
                        }
                        if (renvoi_d_appel_data != undefined) {
                            var add = 0
                            for (var x = 0; x < renvoi_d_appel_data.length / 8; x++) {
                                data_store["iliad"][1][x] = {}
                                for (var y = 0; y < 8; y++) {
                                    if (y == 4)
                                        data_store["iliad"][1][x][y] = renvoi_d_appel_data[y + add] + ': ' + renvoi_d_appel_data[y + add + 1]
                                    else if (y == 5) {} else if (y == 6)
                                        data_store["iliad"][1][x][5] = renvoi_d_appel_data[y + add]
                                    else
                                        data_store["iliad"][1][x][y] = renvoi_d_appel_data[y + add]

                                }
                                add = add + 8;
                            }
                        } else {
                            data_store["iliad"][1] = {};
                            data_store["iliad"][1][0] = 'false';
                        }
                        if (sms_data != undefined) {
                            var add = 0
                            for (var x = 0; x < sms_data.length / 8; x++) {
                                data_store["iliad"][2][x] = {}
                                for (var y = 0; y < 8; y++) {
                                    if (y == 4) {
                                        data_store["iliad"][2][x][y] = sms_data[y + add] + ': ' + sms_data[y + add + 1]
                                    } else if (y == 5) {} else if (y == 6) {
                                        data_store["iliad"][2][x][5] = sms_data[y + add]
                                    } else {
                                        data_store["iliad"][2][x][y] = sms_data[y + add]
                                    }
                                }
                                add = add + 8;
                            }
                        } else {
                            data_store["iliad"][2] = {};
                            data_store["iliad"][2][0] = 'false';
                        }
                        if (data_data != undefined) {
                            var add = 0
                            for (var x = 0; x < data_data.length / 8; x++) {
                                data_store["iliad"][3][x] = {}
                                for (var y = 0; y < 8; y++) {


                                    if (y == 4) {
                                        data_store["iliad"][3][x][y] = data_data[y + add] + ': ' + data_data[y + add + 1]
                                    }

                                    data_store["iliad"][3][x][y] = data_data[y + add]

                                }
                                add = add + 8;
                            }
                        } else {
                            data_store["iliad"][3] = {};
                            data_store["iliad"][3][0] = 'false';
                        }
                    } else {
                        data_store["iliad"] = $('div.no-conso').text();
                    }

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (consumptionroamingdetails == 'true' && token != undefined) {
        var options = {
            umethod: 'GET',
            url: 'https://www.iliad.it/account/consumi-e-credito',
            qs: {
                details: ''
            },
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                referer: 'https://www.iliad.it/account/consumi-e-credito',
                cookie: 'ACCOUNT_SESSID=' + token,
                'accept-language': 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7,pt;q=0.6',
                accept: 'application/json, text/javascript, */*; q=0.01',
                scheme: 'https',
                method: 'GET',
                authority: 'www.iliad.it'
            },
            json: true
        };
        //table-details
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var voix = $('div.table-details').find('div.voix');
                    var renvoi_d_appel = $('div.table-details').find('div.renvoi-d-appel');
                    var sms = $('div.table-details').find('div.sms');
                    var data = $('div.table-details').find('div.data');
                    var table = [];

                    data_store["iliad"]["title"] = {};

                    voix.each(function (i, result) {
                        var title_voix = voix.text().split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["title"][0] = title_voix;

                    });
                    renvoi_d_appel.each(function (i, result) {
                        var title_renvoi_d_appel = renvoi_d_appel.text().split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["title"][1] = title_renvoi_d_appel;
                    });
                    sms.each(function (i, result) {
                        var title_sms = sms.text().split('\n')[2].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["title"][2] = title_sms;

                    });
                    data.each(function (i, result) {
                        var title_data = data.first().find('i').text().replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["title"][3] = title_data;

                    });

                    $('div.table-details')
                        .each(function (index, element) {
                            table = table.concat([$(element).find('div.body').text()]);
                        });

                    var voix_data = table[0].replace(/^\s+|\s+$/gm, '').split('\n');

                    var renvoi_d_appel_data = table[1].replace(/^\s+|\s+$/gm, '').split('\n');

                    var sms_data = table[2].replace(/^\s+|\s+$/gm, '').split('\n');
                    var data_data = table[3].replace(/^\s+|\s+$/gm, '').split('\n');


                    if ($('div.no-conso').attr('style') == 'display:none;') {


                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][2] = {};
                        data_store["iliad"][3] = {};

                        if (voix_data[0] != '') {
                            var add = 0
                            for (var x = 0; x < voix_data.length / 7; x++) {
                                data_store["iliad"][0][x] = {}
                                for (var y = 0; y < 7; y++) {
                                    if (y == 4) {
                                        data_store["iliad"][0][x][y] = voix_data[y + add] + ': ' + voix_data[y + add + 1]
                                    } else if (y == 5) {} else if (y == 6) {
                                        data_store["iliad"][0][x][5] = voix_data[y + add]
                                    } else {
                                        data_store["iliad"][0][x][y] = voix_data[y + add]
                                    }
                                }
                                add = add + 7;
                            }
                        } else {
                            data_store["iliad"][0] = {};
                            data_store["iliad"][0][0] = 'false';
                        }
                        if (renvoi_d_appel_data[0] != '') {
                            var add = 0
                            for (var x = 0; x < renvoi_d_appel_data.length / 7; x++) {
                                data_store["iliad"][1][x] = {}
                                for (var y = 0; y < 7; y++) {
                                    if (y == 4)
                                        data_store["iliad"][1][x][y] = renvoi_d_appel_data[y + add] + ': ' + renvoi_d_appel_data[y + add + 1]
                                    else if (y == 5) {} else if (y == 6)
                                        data_store["iliad"][1][x][5] = renvoi_d_appel_data[y + add]
                                    else
                                        data_store["iliad"][1][x][y] = renvoi_d_appel_data[y + add]

                                }
                                add = add + 7;
                            }
                        } else {
                            data_store["iliad"][1] = {};
                            data_store["iliad"][1][0] = 'false';
                        }
                        if (sms_data[0] != '') {
                            var add = 0
                            for (var x = 0; x < sms_data.length / 7; x++) {
                                data_store["iliad"][2][x] = {}
                                for (var y = 0; y < 7; y++) {
                                    if (y == 4) {
                                        data_store["iliad"][2][x][y] = sms_data[y + add] + ': ' + sms_data[y + add + 1]
                                    } else if (y == 5) {} else if (y == 6) {
                                        data_store["iliad"][2][x][5] = sms_data[y + add]
                                    } else {
                                        data_store["iliad"][2][x][y] = sms_data[y + add]
                                    }
                                }
                                add = add + 7;
                            }
                        } else {
                            data_store["iliad"][2] = {};
                            data_store["iliad"][2][0] = 'false';
                        }
                        if (data_data[0] != '') {
                            var add = 0
                            for (var x = 0; x < data_data.length / 7; x++) {
                                data_store["iliad"][3][x] = {}
                                for (var y = 0; y < 7; y++) {


                                    if (y == 4) {
                                        data_store["iliad"][3][x][y] = data_data[y + add] + ': ' + data_data[y + add + 1]
                                    }

                                    data_store["iliad"][3][x][y] = data_data[y + add]

                                }
                                add = add + 7;
                            }
                        } else {
                            data_store["iliad"][3] = {};
                            data_store["iliad"][3][0] = 'false';
                        }
                    } else {
                        data_store["iliad"] = $('div.no-conso').text();
                    }

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (phonecharge == 'true' && montant != undefined && cbtype != undefined && cbnumero != undefined && cbexpmois != undefined && cbexpannee != undefined && cbcrypto != undefined && token != undefined) {
        formData = {
            montant: montant,
            'cb-type': cbtype,
            'cb-numero': cbnumero,
            'cb-exp-mois': cbexpmois,
            'cb-exp-annee': cbexpannee,
            'cb-crypto': cbcrypto
        }
        var options = {
            url: 'https://www.iliad.it/account/rechargement',
            method: 'POST',
            headers: headers,
            formData: formData
        };
        request(options, function (error, response, body) {
            data_store["iliad"][0] = {}
            if (!error && response.statusCode == 200) {
                try {
                    //flash-error
                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {


                        if ($(result).find('div.flash-error') != null)
                            data_store["iliad"][0] = $(result).find('div.flash-error').text().replace(/^\s+|\s+$/gm, '').replace("Le montant de la transaction est incorrect.\n×", "Informazioni bancarie errate, transazione annullata.");
                        else
                            data_store["iliad"][0] = 'true';

                    })
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }

            }
        });
    } else if (payinfocard == 'true' && token != undefined) {
        var card = [];
        var month = [];
        var year = [];
        var options = {
            url: 'https://www.iliad.it/account/rechargement?montant=5',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');

                    results.each(function (i, result) {
                        $(result)
                            .find('div.card-types')
                            .find('img.creditCard')
                            .each(function (index, element) {
                                card = card.concat([$(element).attr('data-cc-value')]);
                            })
                        $(result)
                            .find('select.mdc-select__input')
                            .each(function (index, element) {
                                if (index == 0)
                                    $(element).find('option')
                                    .each(function (index, element) {
                                        if ($(element).attr('value') != '')
                                            month = month.concat([$(element).attr('value')]);
                                    })
                            })
                        $(result)
                            .find('select.mdc-select__input')
                            .each(function (index, element) {
                                if (index == 1)
                                    $(element).find('option')
                                    .each(function (index, element) {
                                        if ($(element).attr('value') != '')
                                            year = year.concat([$(element).attr('value').replace("20", "")]);
                                    })
                            })
                    });
                    data_store["iliad"][0] = {}
                    data_store["iliad"][1] = {}
                    data_store["iliad"][0] = card;
                    data_store["iliad"][1] = year;
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }

        });
    } else if (payinfoprice == 'true' && token != undefined) {
        var price = [];
        var options = {
            url: 'https://www.iliad.it/account/rechargement',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {
                        $(result).find('select.mdc-select__input').find('option')
                            .each(function (index, element) {
                                if ($(element).attr('value') != '')
                                    price = price.concat([$(element).attr('value')]);
                            })
                        data_store["iliad"][0] = {}
                        data_store["iliad"][0] = price;
                        res.send(data_store);
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (voicemail == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/segreteria-telefonica',
            method: 'GET',
            headers: headers
        };
        var array = [];
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {
                        data_store["iliad"][0] = {}
                        data_store["iliad"][0][0] = $(result).find('h1').first().text().replace(/^\s+|\s+$/gm, '')
                        if ($(result).find('p.text-center').text().replace(/^\s+|\s+$/gm, '') == '') {

                            $(result)
                                .find('div.msg')
                                .each(function (index, element) {
                                    index = index + 1;

                                    data_store["iliad"][index] = {}

                                    data_store["iliad"][index][0] = {}
                                    data_store["iliad"][index][1] = {}
                                    data_store["iliad"][index][2] = {}

                                    data_store["iliad"][index][0] = $(element).find('div.msg__details__tel').text().replace(/^\s+|\s+$/gm, '');
                                    data_store["iliad"][index][1] = $(element).find('div.msg__details__date').text().replace(/^\s+|\s+$/gm, '').replace('\n', ' ').replace('(', '(<span style="color:#cc0000">').replace(')', '</span>)');
                                    data_store["iliad"][index][2] = $(element).find('source').attr('src').split('=')[1];
                                    //data_store["iliad"][index][2] = 'https://www.iliad.it' + $(element).find('source').attr('src');
                                })
                        } else {
                            data_store["iliad"][0] = {}
                            data_store["iliad"][0][0] = $(result).find('p.text-center').text().replace(/^\s+|\s+$/gm, '')
                        }
                        res.send(data_store);
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (deleteaudio == 'true' && idaudio != undefined && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/segreteria-telefonica/messaggio_vocale?id=' + idaudio + '&action=delete',
            method: 'GET',
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                referer: 'https://www.iliad.it/account/conso-et-factures',
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
                    data_store["iliad"][0] = {};
                    data_store["iliad"][1] = {};

                    data_store["iliad"][0] = body[0]["result"]["success"];
                    data_store["iliad"][1] = body[0]["result"]["msg"];

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (idaudio != undefined && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/segreteria-telefonica/messaggio_vocale?id=' + idaudio,
            method: 'GET',
            headers: headers,
            encoding: null
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    res.send(body);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (voicemailoptions == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/segreteria-telefonica',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var status = [];
                    var text = [];
                    var array3 = [];
                    results.each(function (i, result) {

                        var title = $(result).find('h2').first().text().replace(/^\s+|\s+$/gm, '');

                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                text = text.concat([$(element).find('span.as__status__text').text()]);
                            });
                        $(result)
                            .find('div.as__status--active')
                            .each(function (index, element) {
                                status = status.concat([$(element).find('i').attr('class')]);
                            });
                        $(result)
                            .find('div.as__item__name')
                            .each(function (index, element) {
                                array3 = array3.concat([$(element).find('div.inner').text().replace(/^\s+|\s+$/gm, '')]);
                            });

                        var query = [
                            "0",
                            "1",
                            "2",
                            "3",
                        ];

                        var service = {};

                        for (var x = 0; x < 5; x++) {
                            service[x] = [];
                        }
                        for (var x = 0; x < Object.keys(service).length - 1; x++) {
                            service[x][0] = array3[x];
                            service[x][1] = text[x];
                            if (status[x] == 'icon i-check red') {
                                service[x][2] = 'true';
                            } else {
                                service[x][2] = 'false';
                            }
                            service[x][3] = query[x];
                        }

                        for (var x = 0; x < 5; x++) {
                            data_store["iliad"][x] = {};
                        }

                        data_store["iliad"][0][0] = title;

                        for (var x = 0; x < Object.keys(service).length - 1; x++) {
                            for (var y = 0; y < service[x].length; y++) {
                                data_store["iliad"][x + 1][y] = service[x][y];
                            }
                        }
                        res.send(data_store);


                    });
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (changevoicemailoptions == 'true' && activate != undefined && update != undefined && token != undefined) {
        if (codemessagerie != undefined) {
            var options = {
                url: 'https://www.iliad.it /account/segreteria-telefonica',
                method: 'POST',
                headers: headers,
                formData: {
                    update: update,
                    activate: activate,
                    'code-messagerie': codemessagerie
                }
            };
        } else if (announce != undefined) {
            var options = {
                url: 'https://www.iliad.it /account/segreteria-telefonica',
                method: 'POST',
                headers: headers,
                formData: {
                    update: update,
                    activate: activate,
                    announce: announce
                }
            };
        } else {
            var options = {
                url: 'https://www.iliad.it/account/segreteria-telefonica?update=' + update + '&activate=' + activate,
                method: 'GET',
                headers: headers
            };
        }
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    data_store['iliad'][0] = 'true';

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (voicemailreport == 'true' && token != undefined) {
        var options = {
            url: 'https://www.iliad.it/account/segreteria-telefonica',
            method: 'GET',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var title;
                    var text;
                    var text2;
                    var mail = [];
                    var type = [];
                    results.each(function (i, result) {
                        if ($(results).find('div.notifs__list').find('div.notifs__item').find('span.mdc-text-field__label').text().replace(/^\s+|\s+$/gm, '') != '') {
                            data_store['iliad'][0] = {};
                            data_store['iliad'][1] = {};
                            $(results)
                                .find('h2')
                                .each(function (index, element) {
                                    if (index == 1)
                                        data_store['iliad'][0][0] = $(element).text().replace(/^\s+|\s+$/gm, '');
                                })
                            $(results)
                                .find('div.notifs__explain')
                                .find('p')
                                .each(function (index, element) {
                                    if (index == 0)
                                        data_store['iliad'][0][1] = $(element).text().replace(/^\s+|\s+$/gm, '');
                                    else if (index == 1)
                                        data_store['iliad'][0][2] = $(element).text().replace(/^\s+|\s+$/gm, '');
                                })
                            $(results)
                                .find('div.notifs__list')
                                .find('div.notifs__item')
                                .each(function (index, element) {
                                    index = index + 1;
                                    data_store['iliad'][index] = {};
                                    data_store['iliad'][index][0] = $(element).find('span.mdc-text-field__label').text().replace(/^\s+|\s+$/gm, '');
                                    data_store['iliad'][index][1] = $(element).find('input.mdc-text-field__input').attr('value').replace(/^\s+|\s+$/gm, '');
                                    data_store['iliad'][index][2] = $(element).find('span.mdc-select__label').text().replace(/^\s+|\s+$/gm, '');
                                    $(element).find('select.mdc-select__input').find('option').each(function (index2, element) {
                                        if ($(element).attr('selected') == 'selected')
                                            data_store['iliad'][index][3] = $(element).text().replace(/^\s+|\s+$/gm, '');
                                    })
                                })
                        } else {
                            data_store['iliad'][0] = {}
                            data_store['iliad'][0][0] = 'Nessuna e-mail inserita.';
                        }
                        res.send(data_store);
                    })
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (email != undefined && action != undefined && token != undefined) {
        if (type != undefined) {
            var url = 'https://www.iliad.it/account/segreteria-telefonica/notifiche?email=' + email + '&action=' + action + '&type=' + type;
        } else {
            var url = 'https://www.iliad.it/account/segreteria-telefonica/notifiche?email=' + email + '&action=' + action;
        }
        var options = {
            url: url,
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
                    try {
                        data_store['iliad'][0] = body[0]['result']['msg'];
                    } catch (exeption) {
                        data_store['iliad'][0] = body[0]['msg'];
                    }
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                    res.end();
                }
            }
        });
    } else if (alert == 'true') {
        data_store["iliad"][0] = "<b>Se stai utilizzando iliad UNOFFICIAL è stata rimossa dal PlayStore, scarica la nuova app Area personale, per i nuovi aggiornamenti.</b><br /> L’app è stata creata in modo <b>NON</b> ufficiale, iliad S.P.A non è responsabile. L’app prende le informazioni dal sito, se una sezione/testo/oggetto non c’è sul sito non ci sarà nell’app. Ti ricordo inoltre che prima di creare una valutazione sul PlayStore di contattarci su Telegram con <b>@Fast0n</b> o <b>@Mattvoid</b> oppure per email all’indirizzo <b>theplayergame97@gmail.com</b>.<br/>Grazie per l’attenzione."
        res.send(data_store);
    }
});
const server = app.listen(process.env.PORT || 1331, function () {});