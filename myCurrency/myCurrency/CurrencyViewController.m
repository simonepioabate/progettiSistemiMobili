//
//  CurrencyViewController.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import "CurrencyViewController.h"
#import "CountryCurrencyDictionaryManager.h"
#import "DateManager.h"
#import "ApiFastForexManager.h"

@interface CurrencyViewController ()<UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UIDatePicker *toDatePicker;
@property (weak, nonatomic) IBOutlet UIDatePicker *fromDatePicker;
@property (weak, nonatomic) IBOutlet UIButton *fromCurrencyBtn;
@property (weak, nonatomic) IBOutlet UIButton *toCurrencyBtn;
@property (weak, nonatomic) IBOutlet UITableView *tableVw;

@property (strong, nonatomic) CurrencyConverter *currencyConverter;
@property (nonatomic, strong) DateManager *date;

@property (nonatomic, strong) NSArray *tableData;
@property (nonatomic, strong) NSArray *tableDataCurrency;

@property (strong, nonatomic) CountryCurrencyDictionaryManager *countryCurrencyDictionaryManager;
@property (nonatomic, strong) NSDictionary *countryCurrencies;

@end

@implementation CurrencyViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Imposta valori predefiniti per i pulsanti di selezione valuta
    [self.fromCurrencyBtn setTitle:self.currencyConverter.fromCurrencyTitle forState:UIControlStateNormal];
    [self.toCurrencyBtn setTitle:self.currencyConverter.toCurrencyTitle forState:UIControlStateNormal];
    
    // Inizializza il dizionario countryCurrencies con le coppie valuta-paese
    //  key-value
    self.countryCurrencyDictionaryManager = [[CountryCurrencyDictionaryManager alloc] init];
    self.countryCurrencies = self.countryCurrencyDictionaryManager.countryCurrencyDictionary;
    
    [self initDatePicker];
    
    // Imposta il datasource e il delegate della tabella
    self.tableVw.dataSource = self;
    self.tableVw.delegate = self;
    
    [self getHistory];
}

- (void) initDatePicker{
    //inizializzo il gestore della data e successivamente i 2 picker
    self.date = [[DateManager alloc] init];
    
    self.fromDatePicker.minimumDate = self.date.minimumDate;
    self.toDatePicker.minimumDate = self.date.minimumDate;
    self.fromDatePicker.maximumDate = self.date.maximumDate;
    self.toDatePicker.maximumDate = self.date.maximumDate;
    
    [self.fromDatePicker setDate:self.date.weekAgo animated:YES];
    [self.toDatePicker setDate:self.date.yesterday animated:YES];
    
    // Ottieni la data selezionata dal date picker e formattala
    self.date.fromDate = [self.date.dateFormatter stringFromDate:self.fromDatePicker.date];
    self.date.toDate = [self.date.dateFormatter stringFromDate:self.toDatePicker.date];
    
    // Aggiungi un gestore di eventi per quando cambia la data nel date picker
    [self.fromDatePicker addTarget:self action:@selector(datePickerValueChanged:) forControlEvents:UIControlEventValueChanged];
    [self.toDatePicker addTarget:self action:@selector(datePickerValueChanged:) forControlEvents:UIControlEventValueChanged];
    
    // Aggiungi il date picker alla vista
    [self.view addSubview:self.fromDatePicker];
    [self.view addSubview:self.toDatePicker];
}

-(void)getHistory {
    [ApiFastForexManager getHistory:self.currencyConverter date:self.date completion:^(NSArray *value) {
        if (value) {
            self.tableData = value[0];
            self.tableDataCurrency = value[1];
            dispatch_async(dispatch_get_main_queue(), ^{
                // Ricarica la tabella per visualizzare i nuovi dati
                [self.tableVw reloadData];
            });

        } else {
            //error handler
        }
    }];
}

- (IBAction)OnClickDropDown:(id)sender {
    // Crea un controller di avviso per la selezione della valuta di partenza
    UIAlertController *actionSheet = [UIAlertController alertControllerWithTitle:@"Choose Currency" message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    
    for (NSString *currency in self.countryCurrencies) {
        NSString *flag = self.countryCurrencies[currency];
        NSString *title = [NSString stringWithFormat:@"%@: %@", flag, currency]; // Crea il titolo per ogni opzione del menu
        
        // Crea un'azione per l'opzione corrente del menu
        UIAlertAction *action = [UIAlertAction actionWithTitle:title style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            //controllo chi è il bottone in questione per comportarmi di conseguenza
            if(sender == self.fromCurrencyBtn){
                self.currencyConverter.selectedFromCurrencyName = currency;
                self.currencyConverter.selectedFromCurrencyFlag = flag;
                [self.fromCurrencyBtn setTitle:title forState:UIControlStateNormal];
            }else{
                self.currencyConverter.selectedToCurrencyName = currency;
                self.currencyConverter.selectedToCurrencyFlag = flag;
                [self.toCurrencyBtn setTitle:title forState:UIControlStateNormal];
            }
            [self getHistory];
        }];
        // Aggiunge l'azione all'elenco delle azioni del menu a discesa
        [actionSheet addAction:action];
    }
    
    // Aggiungi un'azione per annullare la selezione
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil];
    [actionSheet addAction:cancelAction];
    
    // Presenta il foglio di azioni
    [self presentViewController:actionSheet animated:YES completion:nil];
}

- (void)datePickerValueChanged:(UIDatePicker *)sender {
    // Questo metodo verrà chiamato quando il valore del date picker cambia
    // Ottieni la data selezionata dal date picker e formattala
    if(sender == self.fromDatePicker){
        self.date.fromDate = [self.date.dateFormatter stringFromDate:sender.date];
    } else{
        self.date.toDate = [self.date.dateFormatter stringFromDate:sender.date];
    }
    [self getHistory];

}


#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Restituisce il numero di righe nella tabella basato sulla lunghezza dei dati della tabella
    return self.tableData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    // Metodo per creare e restituire una cella per la tabella
    UITableViewCell *cell = [[UITableViewCell alloc] init];
    
    // Imposta il testo della cella con la valuta e il tasso di cambio corrispondenti
    cell.textLabel.text = [NSString stringWithFormat:@"%@: 1 = %@", self.tableData[indexPath.row], self.tableDataCurrency[indexPath.row]];
    
    return cell;
}

@end
