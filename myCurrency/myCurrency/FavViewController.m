//
//  FavViewController.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//
#import "FavViewController.h"
#import "ViewController.h"
#import "CurrencyConverter.h"

@interface FavViewController ()<UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableVw;

@property (strong, nonatomic) CurrencyConverter *currencyConverter;
@property (nonatomic, strong) NSMutableArray *tableDataFromCurrency;
@property (nonatomic, strong) NSMutableArray *tableDataToCurrency;
@property (nonatomic, strong) NSMutableArray *countryCurrencies;

@end

@implementation FavViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.tableDataFromCurrency = [NSMutableArray array];
    self.tableDataToCurrency = [NSMutableArray array];
    
    // Recupera il dizionario aggiornato da NSUserDefaults
    NSArray *countryCurrenciesNotMutable = [[NSUserDefaults standardUserDefaults] objectForKey:@"currencyFavorite"];
    self.countryCurrencies = [ countryCurrenciesNotMutable mutableCopy];
    
    for (NSDictionary *pair in self.countryCurrencies) {
        [self.tableDataFromCurrency addObject:pair.allKeys.firstObject];
        [self.tableDataToCurrency addObject:pair.allValues.firstObject];
    }

    // Imposta il datasource e il delegate della tabella
    self.tableVw.dataSource = self; //responsabile di fornire i dati che verranno visualizzati nella tabella.
    self.tableVw.delegate = self; //gestisce gli eventi che si verificano all'interno della tabella
}

// Metodo richiamato per ottenere il numero di righe nella sezione della tabella
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.tableDataFromCurrency.count;
}

// Metodo richiamato per creare e restituire una cella per un particolare indice di riga
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    // Crea una nuova cella con uno stile predefinito
    UITableViewCell *cell = [[UITableViewCell alloc] init];
    
    CurrencyConverter *currencyCell = [[CurrencyConverter alloc] initFromTitle:self.tableDataFromCurrency[indexPath.row]
                                                                toCurrency:self.tableDataToCurrency[indexPath.row]];
    // Imposta il testo della cella con i valori ottenuti
    cell.textLabel.text = [NSString stringWithFormat:@"%@ to %@", currencyCell.fromCurrencyTitle, currencyCell.toCurrencyTitle];
    
    // Restituisce la cella appena creata
    return cell;
}

// Metodo richiamato quando l'utente seleziona una riga nella tabella
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    // Ottieni il riferimento allo storyboard principale
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];

    // Istanzia il view controller dallo storyboard con l'identificatore "currencyVw"
    ViewController *controller = (ViewController *)[mainStoryboard instantiateViewControllerWithIdentifier:@"currencyVw"];
    
    controller.currencyConverter = [[CurrencyConverter alloc] initFromTitle:self.tableDataFromCurrency[indexPath.row]
                                                              toCurrency:self.tableDataToCurrency[indexPath.row]];

    // Crea un nuovo stack di navigazione contenente solo il nuovo view controller
    [self.navigationController setViewControllers:@[controller] animated:YES];

}


// Metodo utilizzato per determinare se una riga specifica della tabella può essere modificata
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES; // Restituisce sempre YES per consentire la modifica delle righe
}

// Metodo chiamato quando si compie una modifica alla riga della tabella, come ad esempio l'eliminazione
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {

            // Converte il dizionario recuperato in mutabile per modificarlo
            //NSArray *mutableDictionary = [retrievedDictionary mutableCopy];
            // Ottieni la chiave relativa all'elemento da eliminare
            NSString *vKey = [NSString stringWithFormat:@"%@", self.tableDataFromCurrency[indexPath.row]];
            NSString *vValue = [NSString stringWithFormat:@"%@", self.tableDataToCurrency[indexPath.row]];
        
        NSUInteger rowIndex = NSNotFound;
        //individua la coppia scelta
        for (NSDictionary *pair in self.countryCurrencies) {
            if([pair.allKeys.firstObject isEqualToString:vKey] && [pair.allValues.firstObject isEqualToString:vValue])
            {
                rowIndex = [self.countryCurrencies indexOfObject:pair];
                break;
            }
        }
        //rimuovi elemento
        [self.countryCurrencies removeObjectAtIndex:rowIndex];
            
        // Salva il dizionario aggiornato nell'UserDefaults
        [[NSUserDefaults standardUserDefaults] setObject:self.countryCurrencies forKey:@"currencyFavorite"];
        [[NSUserDefaults standardUserDefaults] synchronize];

        // Rimuovi l'elemento dalle tue strutture dati
        [self.tableDataFromCurrency removeObjectAtIndex:indexPath.row];
        [self.tableDataToCurrency removeObjectAtIndex:indexPath.row];
        [tableView reloadData]; // Ricarica la tabella per mostrare i cambiamenti
    }
}

// Metodo utilizzato per specificare l'altezza di una riga specifica della tabella
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return  60; // Restituisce l'altezza desiderata per tutte le righe della tabella
}

@end
