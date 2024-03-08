//
//  CurrencyConverter.h
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CurrencyConverter : NSObject

- (instancetype)initFromCurrency:(NSString *)fromCurrency withFromFlag:(NSString *)fromFlag toCurrency:(NSString *)toCurrency withToFlag:(NSString *)toFlag;
- (instancetype)init;
- (instancetype)initFromTitle:(NSString *)fromTitle toCurrency:(NSString *)toTitle;
- (NSString *) fromCurrencyTitle;
- (NSString *) toCurrencyTitle;

@property (nonatomic, strong) NSString *selectedFromCurrencyName;
@property (nonatomic, strong) NSString *selectedFromCurrencyFlag;
@property (nonatomic, strong) NSString *selectedFromCurrencyValue;
@property (nonatomic, strong) NSString *selectedToCurrencyName;
@property (nonatomic, strong) NSString *selectedToCurrencyFlag;
@property (nonatomic, strong) NSString *selectedToCurrencyValue;

@end

NS_ASSUME_NONNULL_END
