//
//  ApiFastForexManager.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//
#import "ApiFastForexManager.h"

@implementation ApiFastForexManager


+ (void)callConvertAPI:(CurrencyConverter *)currencyConverter completion:(void (^)(NSString *))callback {
    
    // Creazione dell'URL della richiesta
    NSString *compURL = [NSString stringWithFormat:@"https://api.fastforex.io/convert?from=%@&to=%@&amount=%@&api_key=1ed754992d-89e5aff933-s9v8uk", currencyConverter.selectedFromCurrencyName,currencyConverter.selectedToCurrencyName,currencyConverter.selectedFromCurrencyValue];
    NSURL *url = [NSURL URLWithString:compURL];
    
    // Creazione della richiesta
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    
    // Creazione dell'oggetto di sessione
    NSURLSession *session = [NSURLSession sharedSession];
    
    // Creazione dell'oggetto di task per eseguire la richiesta
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                                completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        if (error) {
            // Gestione degli errori
            NSLog(@"Errore durante la richiesta: %@", error);
            callback(nil); // Passa nil al completamento in caso di errore
        } else {
            // Gestione della risposta
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
            if (httpResponse.statusCode == 200) {
                // La richiesta è stata eseguita con successo
                // Puoi manipolare i dati ricevuti qui
                NSLog(@"Risposta ricevuta: %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
                
                // Ora puoi estrarre un oggetto NSDictionary dai dati della risposta
                NSError *jsonError;
                NSDictionary *responseDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                if (jsonError) {
                    NSLog(@"Errore durante il parsing della risposta JSON: %@", jsonError);
                    callback(nil); // Passa nil al completamento in caso di errore di parsing JSON
                } else {
                    // Ora puoi accedere ai dati all'interno del dizionario JSON
                    NSDictionary *dic = responseDictionary[@"result"];
                    NSLog(@"Dati della risposta: %@", dic);
                    
                    // Si estrae il rate e non il value direttamente per avere più precisione
                    NSString *rate = [dic objectForKey:@"rate"];
                    NSLog(@"Dati del rate: %@", rate);
                    
                    double result = [rate doubleValue]  *  [currencyConverter.selectedFromCurrencyValue doubleValue];
                    
                    // Converti il risultato in una stringa per rappresentarlo come testo, se necessario
                    NSString *resultString = [NSString stringWithFormat:@"%f", result];
                    
                    callback(resultString); // Passa il risultato al completamento
                }
            } else {
                // Altre gestioni dello stato della risposta
                NSLog(@"Stato della risposta non valido: %ld", (long)httpResponse.statusCode);
                callback(nil); // Passa nil al completamento in caso di stato di risposta non valido
            }
        }
    }];
    
    // Avvio del task
    [dataTask resume];
}

+ (void)getHistory:(CurrencyConverter *)currencyConverter date:(DateManager *)date completion:(void (^)(NSArray *))callback{
    // Creazione dell'URL della richiesta
    NSString *compURL = [NSString stringWithFormat:@"https://api.fastforex.io/time-series?from=%@&to=%@&start=%@&end=%@&interval=P1D&api_key=1ed754992d-89e5aff933-s9v8uk", currencyConverter.selectedFromCurrencyName, currencyConverter.selectedToCurrencyName, date.fromDate, date.toDate];
    NSURL *url = [NSURL URLWithString:compURL];
    
    // Creazione della richiesta
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    
    // Creazione dell'oggetto di sessione
    NSURLSession *session = [NSURLSession sharedSession];
    
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                                completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        if (error) {
            // Gestione degli errori
            NSLog(@"Errore durante la richiesta: %@", error);
            callback(nil); // Passa nil al completamento in caso di errore
        } else {
            // Gestione della risposta
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
            if (httpResponse.statusCode == 200) {
                // La richiesta è stata eseguita con successo
                // Puoi manipolare i dati ricevuti qui
                NSLog(@"Risposta ricevuta: %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
                
                // Ora puoi estrarre un oggetto NSDictionary dai dati della risposta
                NSError *jsonError;
                NSDictionary *responseDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                if (jsonError) {
                    NSLog(@"Errore durante il parsing della risposta JSON: %@", jsonError);
                    callback(nil); // Passa nil al completamento in caso di errore di parsing JSON
                } else {
                    
                    NSDictionary *dic = responseDictionary[@"results"];
                    NSDictionary *dateList = [dic objectForKey:currencyConverter.selectedToCurrencyName];

                    // Ottieni le chiavi (date) ordinate in ordine ascendente
                    NSArray *sortedDates = [[dateList allKeys] sortedArrayUsingComparator:^NSComparisonResult(NSString *date1, NSString *date2) {
                        return [date1 compare:date2];
                    }];
                    
                    NSMutableArray *valueSet = [NSMutableArray array];

                    // Crea un nuovo dizionario con le chiavi ordinate e i valori corrispondenti
                    NSMutableDictionary *sortedDateValues = [NSMutableDictionary dictionary];
                    for (NSString *date in sortedDates) {
                        sortedDateValues[date] = dateList[date];
                        [valueSet addObject:dateList[date]];
                    }
                    
                    // Recupera e aggiorna i dati della tabella con le date ordinate e i corrispondenti valori di conversione
                    NSArray *arrayOfArrays = @[sortedDates, valueSet];
                    callback(arrayOfArrays);
                
                }
            } else {
                // Altre gestioni dello stato della risposta
                NSLog(@"Stato della risposta non valido: %ld", (long)httpResponse.statusCode);
                callback(nil); // Passa nil al completamento in caso di stato di risposta non valido
            }
        }
    }];
    
    // Avvio del task
    [dataTask resume];
}

@end
