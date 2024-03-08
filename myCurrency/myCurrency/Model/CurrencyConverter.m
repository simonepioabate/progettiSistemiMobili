//
//  CurrencyConverter.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import "CurrencyConverter.h"

@implementation CurrencyConverter

- (instancetype)initFromCurrency:(NSString *)fromCurrency withFromFlag:(NSString *)fromFlag toCurrency:(NSString *)toCurrency withToFlag:(NSString *)toFlag{
    if (self = [super init]) {
        _selectedFromCurrencyName =  fromCurrency;
        _selectedFromCurrencyFlag = fromFlag;
        _selectedToCurrencyName =  toCurrency;
        _selectedToCurrencyFlag =  toFlag;
        _selectedFromCurrencyValue = @"1";
        _selectedToCurrencyValue = @"0";
    }
    return self;
}

- (instancetype) init{
    return [ self initFromCurrency:@"USD" withFromFlag:@"🇺🇸" toCurrency:@"EUR" withToFlag:@"🇪🇺"];
}

- (instancetype)initFromTitle:(NSString *)fromTitle toCurrency:(NSString *)toTitle{
    if (self = [super init]) {
        [ self setFromTitle:fromTitle ];
        [ self setToTitle:toTitle ];
        _selectedFromCurrencyValue = @"1";
        _selectedToCurrencyValue = @"0";
    }
    return self;
}

//trasformalo in init e poi vai da favViewController
-(void) setFromTitle:(NSString *)title{
    _selectedFromCurrencyFlag = [title substringToIndex:4];
    
    NSRange range = [title rangeOfString:@" "]; //trovo la posizione dello spazio
    NSString *newString = [title substringFromIndex:range.location]; //inizio a prendere il testo dallo spazio
    _selectedFromCurrencyName = [newString stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]; //rimuovo tutti gli spazi e ritorno la stringa
}

-(void) setToTitle:(NSString *)title{
    _selectedToCurrencyFlag = [title substringToIndex:4];
    
    NSRange range = [title rangeOfString:@" "]; //trovo la posizione dello spazio
    NSString *newString = [title substringFromIndex:range.location]; //inizio a prendere il testo dallo spazio
    _selectedToCurrencyName = [newString stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]; //rimuovo tutti gli spazi e ritorno la stringa
}

-(NSString *) fromCurrencyTitle{
    return [NSString stringWithFormat:@"%@: %@", self.selectedFromCurrencyFlag, self.selectedFromCurrencyName];
}

-(NSString *) toCurrencyTitle{
    return [NSString stringWithFormat:@"%@: %@", self.selectedToCurrencyFlag, self.selectedToCurrencyName];
}

@end

