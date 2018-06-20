const express = require('express');
const app = express();
const request = require('request');
const cheerio = require('cheerio');

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
const CURRENT_APP_VERSION = '13';

app.get('/', function (req, res) {
    res.set('text/html; charset=utf-8');
    res.send("<script>window.location.replace('https://github.com/Fast0n/iliad');</script>");
});

// Alert
app.get('/alert', function (req, res) {
    res.set('Content-Type', 'application/json');

    var data_store = { 'iliad': {} };

    data_store["iliad"][0] = "<b>Se stai utilizzando iliad UNOFFICIAL è stata rimossa dal PlayStore, scarica la nuova app Area personale, per i nuovi aggiornamenti.</b><br /> L’app è stata creata in modo <b>NON</b> ufficiale, iliad S.P.A non è responsabile. L’app prende le informazioni dal sito, se una sezione/testo/oggetto non c’è sul sito non ci sarà nell’app. Ti ricordo inoltre che prima di creare una valutazione sul PlayStore di contattarci su Telegram con <b>@Fast0n</b> o <b>@Mattvoid</b> oppure per email all’indirizzo <b>theplayergame97@gmail.com</b>.<br/>Grazie per l’attenzione."
    res.send(data_store);
});

// Login
app.get('/login', function (req, res) {
    res.set('Content-Type', 'application/json');

    var userid = req.query.userid;
    var psw = req.query.password;
    const password = Buffer.from(psw + '', 'base64').toString('utf8');
    var token = req.query.token;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (userid != undefined && password != undefined && token != undefined) {
        var formData = {
            'login-ident': userid,
            'login-pwd': password
        }

        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['login'],
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

                        data_store["iliad"]["version"] = CURRENT_APP_VERSION;
                        data_store["iliad"]["user_name"] = nav[1].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["user_id"] = nav[2].replace(/^\s+|\s+$/gm, '');
                        data_store["iliad"]["user_numtell"] = nav[3].replace(/^\s+|\s+$/gm, '');

                        if (check == 'SIM attivata') {
                            data_store["iliad"]["sim"] = 'true';
                        } else {
                            data_store["iliad"]["sim"] = 'false';
                        }
                        res.send(data_store);
                        return;
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                }
            };
        });
    } else {
        res.sendStatus(400);
    }
});

//recupero password
app.get('/recover', function (req, res) {
    res.set('Content-Type', 'application/json');

    var email = req.query.email;
    var userid = req.query.userid;
    var token = req.query.token;
    var data_store = { 'iliad': {} };
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    var formData = {
        login: userid,
        email: email
    };
    var options = {
        url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recover'],
        method: 'POST',
        formData: formData
    };

    if (email != undefined && userid != undefined && token != undefined) {

        request(options, function (error, response, body) {
            try {
                if (!error && response.statusCode == 200) {

                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {
                        data_store["iliad"][0] = 'true'
                        res.send(data_store);
                    });
                    //data_store["iliad"][0] = ''; //flash-error

                } else {
                    data_store["iliad"][0] = 'true'
                    res.send(body);
                }
            } catch (exeption) {
                res.sendStatus(503);
            }
        })
    } else {
        res.sendStatus(400);
    }
});

// I miei dati personali
app.get('/information', function (req, res) {
    res.set('Content-Type', 'application/json');

    var info = req.query.info;
    var token = req.query.token;
    var puk = req.query.puk;
    var password = req.query.password;
    var new_password = req.query.new_password;
    var new_password_confirm = req.query.new_password_confirm;
    var email = req.query.email;
    var email_confirm = req.query.email_confirm;
    var activation_sim = req.query.activation_sim;

    var data_store = { 'iliad': {} };
    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (info == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'],
            method: 'POST',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    results.each(function (i, result) {
                        $(result)
                            .find('div.infos__content')
                            .each(function (index, element) {
                                array = array.concat([$(element).find('div.infos__text').text()]);
                            });
                        //var puk = $(result).find('span.bulle-info').attr('data-help-content');
                        //var puk_text = array[4].split('\n')[7].replace(/^\s+|\s+$/gm, '');

                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][2] = {};
                        data_store["iliad"][3] = {};
                        data_store["iliad"][4] = {};

                        data_store["iliad"][0][0] = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //address title
                        data_store["iliad"][0][1] = array[0].split('\n')[3].replace(/^\s+|\s+$/gm, ''); //address
                        data_store["iliad"][0][2] = array[0].split('\n')[5].replace(/^\s+|\s+$/gm, ''); //cap
                        data_store["iliad"][0][3] = ""; //icon modifica
                        data_store["iliad"][0][4] = "http://android12.altervista.org/res/ic_adress.png"; //icon

                        data_store["iliad"][1][0] = array[1].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //pay title
                        data_store["iliad"][1][1] = array[1].split('\n')[2].replace(/^\s+|\s+$/gm, ''); //pay method
                        data_store["iliad"][1][2] = ""; //icon modifica
                        data_store["iliad"][1][3] = "http://android12.altervista.org/res/ic_credit_card.png"; //icon


                        data_store["iliad"][2][0] = array[2].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //mail title
                        data_store["iliad"][2][1] = array[2].split('\n')[2].replace(/^\s+|\s+$/gm, ''); //mail
                        data_store["iliad"][2][2] = "http://android12.altervista.org/res/ic_edit.png"; //icon modifica
                        data_store["iliad"][2][3] = "http://android12.altervista.org/res/ic_email.png"; //icon


                        data_store["iliad"][3][0] = array[3].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //password title
                        data_store["iliad"][3][1] = array[3].split('\n')[2].replace(/^\s+|\s+$/gm, ''); //password
                        data_store["iliad"][3][2] = "http://android12.altervista.org/res/ic_edit.png"; //icon modifica
                        data_store["iliad"][3][3] = "http://android12.altervista.org/res/ic_puk.png"; //icon

                        data_store["iliad"][4][0] = array[4].split('\n')[3].replace(/^\s+|\s+$/gm, ''); //puk title
                        data_store["iliad"][4][1] = 'xxxxxx';
                        data_store["iliad"][4][3] = "http://android12.altervista.org/res/ic_password.png"; //icon modifica
                        data_store["iliad"][4][2] = "http://android12.altervista.org/res/ic_show.png" //icon
                        res.send(data_store);

                    });
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else if (puk == 'true' && token != undefined) {
        var options = {
            method: 'GET',
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['information'],
            qs: {
                show: 'puk'
            },
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
            }
        });

    }
    //richiesta per cambiare la mail
    else if (email != undefined && email_confirm != undefined && password != undefined && token != undefined) {
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
                try {
                    data_store['iliad'][0] = 'true';
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }

            }
        });
    } else if (new_password != undefined && new_password_confirm != undefined && password != undefined && token != undefined) {
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
                try {
                    data_store['iliad'][0] = 'true';
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }

            }
        });
    } else {
        res.sendStatus(400);
    }
});

// Attivazione sim
app.get('/sim', function (req, res) {
    res.set('Content-Type', 'application/json');

    var iccid = req.query.iccid;
    var token = req.query.token;
    var activation_sim = req.query.activation_sim;

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    var data_store = { 'iliad': {} };

    if (iccid != undefined && token != undefined) {
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
                }
            }
        });
    } else if (activation_sim == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + 'attivazione-della-sim',
            method: 'POST',
            headers: headers
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    const $ = cheerio.load(body);
                    var results = $('body');
                    var array = [];
                    var array2 = [];
                    var array3 = [];

                    results.each(function (i, result) {

                        data_store["iliad"]["validation"] = {};
                        data_store["iliad"]["shipping"] = {};
                        data_store["iliad"]["sim"] = {};

                        $(result)
                            .find('h2.title')
                            .each(function (index, element) {
                                array = array.concat([$(element).text()]);
                            });
                        $(result)
                            .find('div.grid-l')
                            .find('div.step__text')
                            .each(function (index, element) {
                                array3 = array3.concat([$(element).find('a').text()]);
                                array2 = array2.concat([$(element).find('h4.step__text__title').text()]);
                            });
                        var orderdate = $(result).find('div.step__text').first().text().split('\n')
                        var tracking = $(result).find('a.red').attr('href')
                        var activation = $(result).find('p.explain').text().replace(/^\s+|\s+$/gm, '').split('\n')[0]
                        var check = $(result).find('div.step__text').find('p.green').text();
                        var order_shipped = $(result).find('div.step__text').find('p').html()
                        var title = '';
                        $(result).find('h4.step__text__title')
                            .each(function (index, element) {
                                if (index == 3) {
                                    title = $(element).text();
                                }
                            })
                        //var preparazione = array2[1]

                        if (order_shipped != null)
                            data_store["iliad"]["shipping"][1] = order_shipped; //order shipped
                        else
                            data_store["iliad"]["shipping"][1] = 'Non disponibile';
                        if (tracking != undefined)
                            data_store["iliad"]["shipping"][3] = tracking; //tracking
                        else
                            data_store["iliad"]["shipping"][3] = 'https://www.brt.it/fe-web/definition/errorpage.jsp';
                        if (title != undefined)
                            data_store["iliad"]["sim"][0] = title; //title
                        else
                            data_store["iliad"]["sim"][0] = 'Non disponibile';
                        if (activation != undefined)
                            data_store["iliad"]["sim"][1] = activation; //attivazione
                        else
                            data_store["iliad"]["sim"][1] = 'Non disponibile';

                        if (check == 'SIM attivata') {
                            data_store["iliad"]["sim"][2] = 'true';
                        } else {
                            data_store["iliad"]["sim"][2] = 'false';
                        }

                        data_store["iliad"]["sim"][3] = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //offert
                        data_store["iliad"]["shipping"][2] = array3[2];

                        data_store["iliad"]["validation"][0] = array2[0]; //validation
                        data_store["iliad"]["validation"][1] = orderdate[2].replace(/^\s+|\s+$/gm, ''); //order date
                        data_store["iliad"]["validation"][2] = orderdate[3].replace(/^\s+|\s+$/gm, ''); //date
                        data_store["iliad"]["shipping"][2] = array3[2]; //tracking text
                        data_store["iliad"]["shipping"][0] = array2[2]; //spedizione

                        res.send(data_store)
                        return;

                    });
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

app.get('/credit', function (req, res) {
    res.set('Content-Type', 'application/json');

    var estero = req.query.estero;
    var details = req.query.details;
    var credit = req.query.credit;

    var token = req.query.token;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    if (credit != undefined || estero != undefined && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'],
            method: 'GET',
            headers: headers,
        };
        request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
                try {
                    if (credit != undefined) {
                        var x = 0;
                    } else {
                        var x = 4;
                    }

                    var array2 = [];
                    var array3 = [];

                    const $ = cheerio.load(body);
                    var results = $('body');
                    results.each(function (i, result) {
                        $(result)
                            .find('div.conso__content')
                            .each(function (index, element) {
                                array2 = array2.concat([$(element).find('div.conso__text').text().replace(/^\s+|\s+$/gm, '')]);
                            });
                        $(result)
                            .find('div.conso__icon')
                            .each(function (index, element) {
                                if ($(element).find('div.wrapper-align').text().replace(/^\s+|\s+$/gm, '').split('\n')[2] != undefined) {
                                    array3 = array3.concat([$(element).find('div.wrapper-align').text().replace(/^\s+|\s+$/gm, '').split('\n')[2]]);
                                } else {
                                    array3 = array3.concat([$(element).find('div.wrapper-align').text().replace(/^\s+|\s+$/gm, '')]);
                                }
                            });
                        var title = $(result).find('h2').find('b.red').text().replace(/^\s+|\s+$/gm, '');
                        var title2;
                        $(result).find('div.table-montant').find('div.label').each(function (index, element) {
                            if (index == 1)
                                title2 = $(element).text().replace(/^\s+|\s+$/gm, '')
                        });

                        data_store["iliad"][0] = {};

                        data_store["iliad"][0][0] = title + '&' + title2; //titole credito
                        data_store["iliad"][0][1] = 'true'; //ricarica button
                        data_store["iliad"][0][2] = 'true'; //info consumi button

                        var icon = [
                            "http://android12.altervista.org/res/ic_call.png",
                            "http://android12.altervista.org/res/ic_sms.png",
                            "http://android12.altervista.org/res/ic_gb.png",
                            "http://android12.altervista.org/res/ic_mms.png"
                        ];

                        for (var y = 1; y < 5; y++) {
                            var z = y - 1;
                            data_store['iliad'][y] = {};
                            data_store["iliad"][y][0] = array2[x + z].split('\n')[0]; //tipo
                            data_store["iliad"][y][1] = array2[x + z].split('\n')[1]; //consumi
                            data_store["iliad"][y][2] = array3[x + z]; //titole
                            data_store["iliad"][y][3] = icon[y - 1] //icon

                        }

                        res.send(data_store);
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else if (details == 'true' && token != undefined) {
        var options = {
            umethod: 'GET',
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'],
            qs: {
                details: ''
            },
            headers: {
                'Cache-Control': 'no-cache',
                'x-requested-with': 'XMLHttpRequest',
                referer: ILIAD_BASE_URL + ILIAD_OPTION_URL['credit'],
                cookie: 'ACCOUNT_SESSID=' + token,
                'accept-language': 'it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7,pt;q=0.6',
                accept: 'application/json, text/javascript,; q=0.01',
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

                    var type = ['div.voix', 'div.renvoi-d-appel', 'div.sms', 'div.data'];
                    var data = {};

                    var table = [];

                    $('div.table-details')
                        .each(function (index, element) {
                            table = table.concat([$(element).find('div.body').text()]);
                        });

                    data_store["iliad"]["title"] = {};

                    for (var x = 0; x < type.length; x++) {
                        if ($('div.table-details').find(type[x]) != '') {
                            $('div.table-details').find(type[x]).each(function (i, result) {
                                data_store["iliad"]["title"][x] = $('div.table-details').find(type[x]).text().replace(/^\s+|\s+$/gm, '');
                            });
                        } else {
                            data_store["iliad"]["title"][x] = '';
                        }
                    }

                    for (var x = 0; x < table.length; x++) {
                        if (table[x] != undefined) {
                            data[x] = table[x].replace(/^\s+|\s+$/gm, '').split('\n');
                        } else {
                            data[x] = undefined;
                        }
                    }
                    //OK
                    if ($('div.no-conso').attr('style') == 'display:none;') {

                        for (var z = 0; z < 4; z++) {
                            data_store["iliad"][z] = {};
                            var add = 0
                            if (data[z] != undefined) {
                                if (z == 1) {
                                    var i = 7;
                                    var t = 4;
                                } else {
                                    var i = 8;
                                    var t = 5;
                                }
                                for (var x = 0; x < data[z].length / i; x++) {
                                    data_store["iliad"][z][x] = {}
                                    for (var y = 0; y < i; y++) {
                                        if (y == t) {
                                            data_store["iliad"][z][x][y] = data[z][y + add] + ': ' + data[z][y + add + 1]
                                        } else if (y == t + 1) { } else if (y == t + 2) {
                                            data_store["iliad"][z][x][t + 1] = data[z][y + add]
                                        } else {
                                            data_store["iliad"][z][x][y] = data[z][y + add]
                                        }
                                    }
                                    add = add + i;
                                }
                            } else {
                                data_store["iliad"][z] = {};
                                data_store["iliad"][z][0] = "";
                            }
                        }
                    } else {
                        data_store["iliad"] = $('div.no-conso').text();
                    }
                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(exeption);
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

// I Miei Servizi
app.get('/services', function (req, res) {
    res.set('Content-Type', 'application/json');

    var token = req.query.token;
    var services = req.query.services;
    var change_services = req.query.change_services;
    var update = req.query.update;
    var activate = req.query.activate;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };
    if (services == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['services'],
            method: 'GET',
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
                }
            }
        });
    } else if (change_services == 'true' && activate != undefined && update != undefined && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['services'] + '?update=' + update + '&activate=' + activate,
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
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

//Document
app.get('/document', function (req, res) {
    res.set('Content-Type', 'application/json');

    var doc = req.query.doc;
    var token = req.query.token;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (doc == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['document'],
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
                                array2 = array2.concat([$(element).find('div.conso__text').find('a').attr('href')]);
                            });

                        data_store["iliad"][0] = {};
                        data_store["iliad"][1] = {};
                        data_store["iliad"][0][0] = array[0].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //condition title
                        data_store["iliad"][0][1] = array[0].split('\n')[2].replace(/^\s+|\s+$/gm, ''); //condition text
                        data_store["iliad"][0][2] = 'https://www.iliad.it' + array2[0];; //condition doc
                        data_store["iliad"][1][0] = array[1].split('\n')[1].replace(/^\s+|\s+$/gm, ''); //price title
                        data_store["iliad"][1][1] = array[1].split('\n')[2].replace(/^\s+|\s+$/gm, ''); //price text
                        data_store["iliad"][1][2] = 'https://www.iliad.it' + array2[1]; //price doc
                        res.send(data_store);
                    });
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

// Le mie opzioni
app.get('/options', function (req, res) {
    res.set('Content-Type', 'application/json');

    var option = req.query.option;
    var token = req.query.token;
    var update = req.query.update;
    var activate = req.query.activate;
    var change_options = req.query.change_options;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (option == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['options'],
            method: 'GET',
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
                }
            }
        });
    } else if (change_options == 'true' && update != undefined && activate != undefined && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['options'] + '?update=' + update + '&activate=' + activate,
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
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

// Ricarica credito
app.get('/recharge', function (req, res) {
    var cbtype = req.query.cbtype;
    var cbnumero = req.query.cbnumero;
    var montant = req.query.montant;
    var cbexpmois = req.query.cbexpmois;
    var cbexpannee = req.query.cbexpannee;
    var cbcrypto = req.query.cbcrypto;
    var payinfoprice = req.query.payinfoprice;
    var payinfocard = req.query.payinfocard;
    var token = req.query.token;

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (montant != undefined && cbtype != undefined && cbnumero != undefined && cbexpmois != undefined && cbexpannee != undefined && cbcrypto != undefined && token != undefined) {
        // Esecuzione ricarica
        var formData = {
            montant: montant,
            'cb-type': cbtype,
            'cb-numero': cbnumero,
            'cb-exp-mois': cbexpmois,
            'cb-exp-annee': cbexpannee,
            'cb-crypto': cbcrypto
        }
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'],
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
                }

            }
        });
    } else if (payinfocard == 'true' && token != undefined) {
        // Informazione per la ricarica
        var card = [];
        var month = [];
        var year = [];
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'] + '?montant=5',
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
                                if (index == 0) {
                                    $(element).find('option')
                                        .each(function (index, element) {
                                            if ($(element).attr('value') != '')
                                                month = month.concat([$(element).attr('value')]);
                                        })
                                }
                                else if (index == 1) {
                                    $(element).find('option')
                                        .each(function (index, element) {
                                            if ($(element).attr('value') != '')
                                                year = year.concat([$(element).attr('value').replace("20", "")]);
                                        })
                                }
                            })
                    });
                    data_store["iliad"][0] = {}
                    data_store["iliad"][1] = {}
                    data_store["iliad"][0] = card;
                    data_store["iliad"][1] = year;

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }

        });
    } else if (payinfoprice == 'true' && token != undefined) {
        // Informazione sulle possibilità di importo per la ricarica
        var price = [];
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['recharge'],
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
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});

// Segreteria
app.get('/voicemail', function (req, res) {
    res.set('Content-Type', 'application/json');

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

    var data_store = { 'iliad': {} };

    var headers = {
        'cookie': 'ACCOUNT_SESSID=' + token //cookie di accesso
    };

    if (voicemail == 'true' && token != undefined) {
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
                }
            }
        });
    } else if (deleteaudio == 'true' && idaudio != undefined && token != undefined) {
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
                    data_store["iliad"][0] = {};
                    data_store["iliad"][1] = {};

                    data_store["iliad"][0] = body[0]["result"]["success"];
                    data_store["iliad"][1] = body[0]["result"]["msg"];

                    res.send(data_store);
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else if (idaudio != undefined && token != undefined) {
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
                    res.send(body);
                } catch (exeption) {
                    res.sendStatus(503);
                }
            }
        });
    } else if (voicemailoptions == 'true' && token != undefined) {
        // Richiesta opzioni segreteria
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'],
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
                }
            }
        });
    } else if (voicemailreport == 'true' && token != undefined) {
        var options = {
            url: ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'],
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
                }
            }
        });
    } else if (email != undefined && action != undefined && token != undefined) {
        //richiesta per aggiungere/eliminare le mail per la notifica della segreteria  
        var url = ILIAD_BASE_URL + ILIAD_OPTION_URL['voicemail'] + '/notifiche?email=' + email + '&action=' + action;

        if (type != undefined) { url += '&type=' + type; }

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
                }
            }
        });
    } else {
        res.sendStatus(400);
    }
});


const server = app.listen(process.env.PORT || 1331, function () { });