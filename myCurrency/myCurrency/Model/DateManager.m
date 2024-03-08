//
//  DateManager.m
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import "DateManager.h"

@implementation DateManager


- (instancetype)init{
    if (self = [super init]) {
        NSCalendar *calendar = [NSCalendar currentCalendar];
        NSDate *today = [NSDate date]; // Ottieni la data di oggi
        
        NSDateComponents *dateComponents = [[NSDateComponents alloc] init];
        
        [dateComponents setDay:-1]; //1 giorno fa
        _yesterday = [calendar dateByAddingComponents:dateComponents toDate:today options:0];
        
        [dateComponents setDay:-7]; // 1 settimana fa
        _weekAgo = [calendar dateByAddingComponents:dateComponents toDate:today options:0];
        
        [dateComponents setYear:-3]; // 3 anni fa rispetto ad oggi
        _minimumDate = [calendar dateByAddingComponents:dateComponents toDate:today options:0];
        
        _maximumDate = _yesterday;
        
        _dateFormatter = [[NSDateFormatter alloc] init];
        [_dateFormatter setDateFormat:@"yyyy-MM-dd"];
    }
    return self;
}

@end
